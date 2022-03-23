package com.github.kusaanko.atomcamclient.api.device;

import com.github.kusaanko.atomcamclient.api.av.P2PAv;
import com.github.kusaanko.atomcamclient.api.av.audio.AudioData;
import com.github.kusaanko.atomcamclient.api.av.video.FrameData;
import com.github.kusaanko.atomcamclient.api.device.params.CameraParams;
import com.github.kusaanko.atomcamclient.ffmpeg.FFmpegDecoder;

public class ATOMCamera {
    private final ATOMDevice device;
    private P2PAv p2PAv;
    private FFmpegDecoder ffmpegDecoder;
    private boolean isVerified;

    public static enum STATUS {VERIFYING, CONNECTING, WAITING}

    private STATUS status = STATUS.CONNECTING;

    private boolean isWorking = true;
    private Thread controlThread = new Thread() {
        @Override
        public void run() {
            while (isWorking) {
                p2PAv.readControl();
            }
        }
    };

    public ATOMCamera(ATOMDevice device) {
        this.device = device;
    }

    public boolean createConnection() {
        this.setStatus(STATUS.CONNECTING);
        this.p2PAv = new P2PAv(this);
        CameraParams params = (CameraParams) this.device.getDeviceParams();
        boolean success = this.p2PAv.create(this.device.getMac(), params.getFormattedP2PId(), params.getScloudAddress(), this.device.getEnr());
        if (!success) {
            return false;
        }
        this.p2PAv.startConnectCamera();
        this.setStatus(STATUS.VERIFYING);
        return true;
    }

    public void enableVideo() {
        this.p2PAv.enableVideo();
        this.setStatus(STATUS.WAITING);
    }

    public void startPlayback(long timestamp) {
        this.p2PAv.startPlayback(timestamp);
    }

    public void stopPlayback() {
        this.p2PAv.stopPlayback();
    }

    public FrameData getRawFrame() {
        FrameData frameData = new FrameData();
        frameData.setData(this.p2PAv.getFrame());
        if (this.ffmpegDecoder == null && frameData.getData() != null) {
            this.ffmpegDecoder = new FFmpegDecoder(frameData.getData());
        }
        if (this.ffmpegDecoder != null) {
            frameData.setFps(this.ffmpegDecoder.getFps());
        }
        return frameData;
    }

    public FrameData getFrame() {
        FrameData frameData = this.getRawFrame();
        if (this.ffmpegDecoder != null) {
            frameData = this.ffmpegDecoder.decodeFrame(frameData.getData());
        }
        return frameData;
    }

    public AudioData getAudio() {
        return this.p2PAv.getAudio();
    }

    public void start() {
        this.isWorking = true;
        this.controlThread.start();
    }

    public void close() {
        this.p2PAv.disconnect();
        this.stop();
        if (this.ffmpegDecoder != null) {
            this.ffmpegDecoder.free();
        }
    }

    public void stop() {
        this.isWorking = false;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}
