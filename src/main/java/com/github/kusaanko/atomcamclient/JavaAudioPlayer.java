package com.github.kusaanko.atomcamclient;

import com.github.kusaanko.atomcamclient.api.av.audio.AudioData;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Queue;

public class JavaAudioPlayer {
    private int sampleRate;
    private int pcmSize;
    private int channels;

    private AudioFormat format;
    private SourceDataLine dataLine;
    private Queue<AudioData> audioQueue;

    private boolean isAudioPlaying;
    private float volume = 1.0f;
    private Thread audioPlayThread = new Thread(() -> {
        while (isAudioPlaying) {
            AudioData data = this.audioQueue.poll();
            if (data != null) {
                byte[] bytes = data.getData();
                if (this.volume != 1.0f) {
                    ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
                    ByteBuffer newBuffer = ByteBuffer.allocate(bytes.length).order(ByteOrder.LITTLE_ENDIAN);
                    while (buffer.hasRemaining()) {
                        short newValue = buffer.getShort();
                        if (newValue * volume > Short.MAX_VALUE) {
                            newValue = Short.MAX_VALUE;
                        } else if (newValue * volume < Short.MIN_VALUE) {
                            newValue = Short.MIN_VALUE;
                        } else {
                            newValue = (short) (newValue * volume);
                        }
                        newBuffer.putShort(newValue);
                    }
                    bytes = newBuffer.array();
                }
                dataLine.write(bytes, 0, bytes.length);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public JavaAudioPlayer(int sampleRate, int pcmSize, int channels) {
        this.sampleRate = sampleRate;
        this.pcmSize = pcmSize;
        this.channels = channels;
    }

    public JavaAudioPlayer(AudioData audioData) {
        this.sampleRate = audioData.getSampleRate();
        this.pcmSize = audioData.getPcmSize();
        this.channels = audioData.getChannels();
    }

    public void initialize(AudioData data) {
        this.format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                data.getSampleRate(), data.getPcmSize(), data.getChannels(),
                (data.getPcmSize() / 8) * data.getChannels(), data.getSampleRate(), false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line matching " + info + " not supported.");
            return;
        }
        try {
            dataLine = (SourceDataLine) AudioSystem.getLine(info);
            dataLine.open(this.format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        this.audioQueue = new ArrayDeque<>();
    }

    public void play() {
        this.isAudioPlaying = true;
        new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.audioPlayThread.start();
        }).start();
        dataLine.start();
    }

    public void stop() {
        this.isAudioPlaying = false;
        this.audioQueue.clear();
    }

    public void queue(AudioData data) {
        this.audioQueue.add(data);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
