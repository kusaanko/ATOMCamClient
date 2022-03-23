package com.github.kusaanko.atomcamclient.api.av.video;

import java.util.Arrays;

public class FrameInfo {
    public static final int MEDIA_CODEC_VIDEO_H263 = 77;
    public static final int MEDIA_CODEC_VIDEO_H264 = 78;
    public static final int MEDIA_CODEC_VIDEO_H264_AP = 143;
    public static final int MEDIA_CODEC_VIDEO_H265 = 80;
    public static final int MEDIA_CODEC_VIDEO_MJPEG = 79;
    public static final int MEDIA_CODEC_VIDEO_MPEG4 = 76;

    private int audioChannelMono;
    private int audioSample;
    private byte camIndex;
    private short codecID;
    private int encodingPcmBit;
    private byte flags;
    private int fps;
    private int frameLen;
    private String frameMac;
    private int frameNum;
    private int frameToken;
    private byte onlineNum;
    private byte[] reserve1;
    private int reserve2;
    private int resolution;
    private int timestamp;

    public FrameInfo(byte[] info, int length) {
        this.reserve1 = new byte[3];
        this.frameMac = "";
        this.frameToken = 0;
        this.resolution = 0;
        this.audioSample = 8000;
        this.encodingPcmBit = 0;
        this.fps = 15;
        if (info != null && length >= 23) {
            this.codecID = (short) ((info[0] & 0xFF) + ((info[1] & 0xFF << 8)));
            this.flags = info[2];
            this.camIndex = info[3];
            this.onlineNum = info[4];
            this.fps = info[5];
            System.arraycopy(info, 5, this.reserve1, 0, 3);
            this.resolution = this.reserve1[1];
            this.reserve2 = bytesToInteger(info, 8);
            this.timestamp = bytesToInteger(info, 12);
            this.frameLen = bytesToInteger(info, 16);
            this.frameNum = bytesToInteger(info, 20);
            this.audioChannelMono = this.flags & 1;
            this.encodingPcmBit = (this.flags & 2) >> 1;
            this.audioSample = (this.flags & 252) >> 2;
            if (length >= 39) {
                this.frameMac = new String(Arrays.copyOfRange(info, 24, 35));
                this.frameToken = bytesToInteger(info, 36);
            }
        }
    }

    public static int bytesToInteger(byte[] bytes, int i) throws StringIndexOutOfBoundsException {
        return (bytes[i] & 0xFF) | ((bytes[i + 1] & 0xFF) << 8) | ((bytes[i + 2] & 0xFF) << 16) | ((bytes[i + 3] & 0xFF) << 24);
    }

    public int getAudioChannelMono() {
        return audioChannelMono;
    }

    public void setAudioChannelMono(int audioChannelMono) {
        this.audioChannelMono = audioChannelMono;
    }

    public int getAudioSample() {
        return audioSample;
    }

    public void setAudioSample(int audioSample) {
        this.audioSample = audioSample;
    }

    public byte getCamIndex() {
        return camIndex;
    }

    public void setCamIndex(byte camIndex) {
        this.camIndex = camIndex;
    }

    public short getCodecID() {
        return codecID;
    }

    public void setCodecID(short codecID) {
        this.codecID = codecID;
    }

    public int getEncodingPcmBit() {
        return encodingPcmBit;
    }

    public void setEncodingPcmBit(int encodingPcmBit) {
        this.encodingPcmBit = encodingPcmBit;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getFrameLen() {
        return frameLen;
    }

    public void setFrameLen(int frameLen) {
        this.frameLen = frameLen;
    }

    public String getFrameMac() {
        return frameMac;
    }

    public void setFrameMac(String frameMac) {
        this.frameMac = frameMac;
    }

    public int getFrameNum() {
        return frameNum;
    }

    public void setFrameNum(int frameNum) {
        this.frameNum = frameNum;
    }

    public int getFrameToken() {
        return frameToken;
    }

    public void setFrameToken(int frameToken) {
        this.frameToken = frameToken;
    }

    public byte getOnlineNum() {
        return onlineNum;
    }

    public void setOnlineNum(byte onlineNum) {
        this.onlineNum = onlineNum;
    }

    public byte[] getReserve1() {
        return reserve1;
    }

    public void setReserve1(byte[] reserve1) {
        this.reserve1 = reserve1;
    }

    public int getReserve2() {
        return reserve2;
    }

    public void setReserve2(int reserve2) {
        this.reserve2 = reserve2;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FrameInfo{" +
                "audioChannelMono=" + audioChannelMono +
                ", audioSample=" + audioSample +
                ", camIndex=" + camIndex +
                ", codecID=" + codecID +
                ", encodingPcmBit=" + encodingPcmBit +
                ", flags=" + flags +
                ", fps=" + fps +
                ", frameLen=" + frameLen +
                ", frameMac='" + frameMac + '\'' +
                ", frameNum=" + frameNum +
                ", frameToken=" + frameToken +
                ", onlineNum=" + onlineNum +
                ", reserve1=" + Arrays.toString(reserve1) +
                ", reserve2=" + reserve2 +
                ", resolution=" + resolution +
                ", timestamp=" + timestamp +
                '}';
    }
}
