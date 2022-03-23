package com.github.kusaanko.atomcamclient.api.av.video;

public class FrameData {
    private int[] fps;
    private long frameNumber;
    private int width;
    private int height;
    private byte[] data;

    public FrameData() {
    }

    public int[] getFps() {
        return fps;
    }

    public void setFps(int[] fps) {
        this.fps = fps;
    }

    public long getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(long frameNumber) {
        this.frameNumber = frameNumber;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
