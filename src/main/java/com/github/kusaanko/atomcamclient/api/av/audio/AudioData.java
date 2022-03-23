package com.github.kusaanko.atomcamclient.api.av.audio;

import com.github.kusaanko.atomcamclient.api.av.video.FrameInfo;

public class AudioData {
    public static final int MEDIA_CODEC_AUDIO_G711_ULAW = 137;
    public static final int MEDIA_CODEC_AUDIO_ADPCM = 139;
    public static final int MEDIA_CODEC_AUDIO_PCM = 140;
    public static final int MEDIA_CODEC_AUDIO_AAC = 141;
    public static final int MEDIA_CODEC_AUDIO_MP3 = 142;
    public static final int MEDIA_CODEC_AUDIO_G711_ALAW = 143;
    public static final int MEDIA_CODEC_AUDIO_AAC_ELD = 144;
    public static final int MEDIA_CODEC_AUDIO_AAC_LC = 145;

    private int sampleRate;
    private int channels;
    private int pcmSize;
    private byte[] data;
    private int codecId;

    public AudioData(int sampleRate, int channels, int pcmSize, int codecId) {
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.pcmSize = pcmSize;
        this.codecId = codecId;
    }

    public AudioData(FrameInfo frameInfo) {
        if (frameInfo.getAudioSample() == 0) {
            this.setSampleRate(8000);
        } else {
            this.setSampleRate(11025);
        }
        if (frameInfo.getAudioChannelMono() == 0) {
            this.setChannels(2);
        } else {
            this.setChannels(1);
        }
        if (frameInfo.getEncodingPcmBit() == 1) {
            this.setPcmSize(16);
        } else {
            this.setPcmSize(8);
        }
        this.setCodecId(frameInfo.getCodecID());
        if (frameInfo.getCodecID() == AudioData.MEDIA_CODEC_AUDIO_G711_ALAW) {
            this.setChannels(1);
            this.setPcmSize(8);
        }
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getPcmSize() {
        return pcmSize;
    }

    public void setPcmSize(int pcmSize) {
        this.pcmSize = pcmSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getCodecId() {
        return codecId;
    }

    public void setCodecId(int codecId) {
        this.codecId = codecId;
    }
}
