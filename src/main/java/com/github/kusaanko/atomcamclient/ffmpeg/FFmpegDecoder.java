package com.github.kusaanko.atomcamclient.ffmpeg;

import com.github.kusaanko.atomcamclient.api.av.video.FrameData;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVIOContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avformat.Read_packet_Pointer_BytePointer_int;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.swscale.SwsContext;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.global.swscale.*;

public class FFmpegDecoder {
    private AVFormatContext format_context;
    private AVStream video_stream;
    private AVCodec codec;
    private AVCodecContext codec_context;
    private AVFrame bgr_frame;
    private SwsContext sws_ctx;
    private AVFrame video_frame;
    private AVPacket packet;

    static {
        av_log_set_level(AV_LOG_QUIET);
    }

    public FFmpegDecoder(byte[] rawImage) {
        final int[] pos = {0};
        Read_packet_Pointer_BytePointer_int readPacket = new Read_packet_Pointer_BytePointer_int() {
            @Override
            public int call(Pointer opaque, BytePointer buf, int buf_size) {
                int remain = rawImage.length - pos[0];
                if (remain < buf_size) {
                    buf_size = remain;
                }
                byte[] buff = Arrays.copyOfRange(rawImage, pos[0], pos[0] + buf_size);
                buf.put(buff);
                pos[0] += buf_size;
                return buf_size;
            }
        };
        format_context = new AVFormatContext(null);
        format_context = avformat_alloc_context();
        AVIOContext avioContext = avio_alloc_context(new BytePointer(av_malloc(4096)), 4096, 0, null, readPacket, null, null);
        format_context.pb(avioContext);
        if (avformat_open_input(format_context, (String) null, null, null) != 0) {
            throw new IllegalArgumentException("error");
        }
        if (avformat_find_stream_info(format_context, (AVDictionary) null) < 0) {
            throw new IllegalArgumentException("avformat_find_stream_info failed");
        }
        video_stream = null;
        for (int i = 0; i < format_context.nb_streams(); ++i) {
            if (format_context.streams(i).codecpar().codec_type() == AVMEDIA_TYPE_VIDEO) {
                video_stream = format_context.streams(i);
                break;
            }
        }
        if (video_stream == null) {
            throw new IllegalArgumentException("No video stream ...");
        }
        codec = avcodec_find_decoder(video_stream.codecpar().codec_id());
        if (codec == null) {
            throw new IllegalArgumentException("No supported decoder ...");
        }
        codec_context = avcodec_alloc_context3(codec);
        if (codec_context == null) {
            throw new IllegalArgumentException("avcodec_alloc_context3 failed");
        }
        if (avcodec_parameters_to_context(codec_context, video_stream.codecpar()) < 0) {
            throw new IllegalArgumentException("avcodec_parameters_to_context failed");
        }
        if (avcodec_open2(codec_context, codec, (AVDictionary) null) != 0) {
            throw new IllegalArgumentException("avcodec_open2 failed");
        }
        codec_context.thread_count(0);
        bgr_frame = av_frame_alloc();
        if (bgr_frame == null) {
            throw new IllegalArgumentException("memory allocation failed");
        }

        int numBytes = av_image_get_buffer_size(AV_PIX_FMT_BGR24, codec_context.width(),
                codec_context.height(), 1);
        BytePointer buffer = new BytePointer(av_malloc(numBytes));
        sws_ctx = sws_getContext(
                codec_context.width(), codec_context.height()
                , codec_context.pix_fmt()
                , codec_context.width(), codec_context.height()
                , AV_PIX_FMT_BGR24, SWS_BICUBIC, null, null, (double[]) null);

        if (sws_ctx == null) {
            throw new IllegalStateException("Can not use sws");
        }

        av_image_fill_arrays(bgr_frame.data(), bgr_frame.linesize(),
                buffer, AV_PIX_FMT_BGR24, codec_context.width(), codec_context.height(), 1);

        video_frame = av_frame_alloc();
        packet = av_packet_alloc();
        while (av_read_frame(format_context, packet) == 0) {
            if (packet.stream_index() == video_stream.index()) {
                if (avcodec_send_packet(codec_context, packet) != 0) {
                    throw new IllegalArgumentException("avcodec_send_packet failed\n");
                }
                av_frame_unref(video_frame);
                if (avcodec_receive_frame(codec_context, video_frame) == 0) {
                }
            }
            av_packet_unref(packet);
        }
    }

    public int getWidth() {
        return this.codec_context.width();
    }

    public int getHeight() {
        return this.codec_context.height();
    }

    public int[] getFps() {
        return new int[]{this.codec_context.framerate().num(), this.codec_context.framerate().den()};
    }

    public FrameData decodeFrame(byte[] rawImage) {
        if (rawImage == null) return null;
        packet.data(new BytePointer(rawImage));
        packet.size(rawImage.length);
        if (packet.stream_index() == video_stream.index()) {
            if (avcodec_send_packet(codec_context, packet) != 0) {
                throw new IllegalArgumentException("avcodec_send_packet failed\n");
            }
            av_frame_unref(video_frame);
            if (avcodec_receive_frame(codec_context, video_frame) == 0) {
                // convert frame to rgb
                sws_scale(sws_ctx, video_frame.data()
                        , video_frame.linesize(), 0, video_frame.height()
                        , bgr_frame.data(), bgr_frame.linesize());
                BytePointer pointer = bgr_frame.data(0);
                pointer.capacity((long) bgr_frame.linesize(0) * video_frame.height());
                pointer.limit((long) bgr_frame.linesize(0) * video_frame.height());
                pointer.position(0);
                ByteBuffer buffer = pointer.asByteBuffer();
                byte[] array = new byte[buffer.remaining()];
                buffer.get(array);
                pointer.releaseReference();
                av_packet_unref(packet);
                FrameData frameData = new FrameData();
                frameData.setData(array);
                frameData.setWidth(video_frame.width());
                frameData.setHeight(video_frame.height());
                frameData.setFps(this.getFps());
                return frameData;
            }
        }
        av_packet_unref(packet);
        return null;
    }

    public void free() {
        av_packet_free(this.packet);
        av_frame_free(this.video_frame);
        av_frame_free(this.bgr_frame);
        avcodec_free_context(this.codec_context);
        avformat_free_context(this.format_context);
        sws_freeContext(this.sws_ctx);
    }
}
