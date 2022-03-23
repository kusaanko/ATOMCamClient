package com.github.kusaanko.atomcamclient;

import com.github.kusaanko.atomcamclient.api.av.video.FrameData;
import com.github.kusaanko.atomcamclient.api.device.ATOMCamera;
import com.github.kusaanko.atomcamclient.opengl.ImageRenderer;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import java.awt.*;

public class VideoPlayer extends JFrame {
    private final ATOMCamera camera;
    private ImageRenderer imageRenderer;
    private boolean stopGetVideoThread = true;
    private boolean isStreaming = false;
    private final Thread getVideoThread = new Thread() {
        @Override
        public void run() {
            while (!stopGetVideoThread) {
                FrameData frameData = camera.getFrame();
                isStreaming = true;
                imageRenderer.putImage(frameData.getData(), frameData.getWidth(), frameData.getHeight());
            }
        }
    };

    public VideoPlayer(ATOMCamera camera) {
        super();
        this.camera = camera;
        GLData glData = new GLData();
        this.imageRenderer = new ImageRenderer(glData);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());
        this.add(panel);
        JLabel statusLabel = new JLabel("接続中...");
        //panel.add(statusLabel, BorderLayout.CENTER);
        new Thread(() -> {
            while (!isStreaming) {
                if (camera.getStatus() == ATOMCamera.STATUS.CONNECTING) {
                    statusLabel.setText("接続中...");
                } else if (camera.getStatus() == ATOMCamera.STATUS.VERIFYING) {
                    statusLabel.setText("認証中...");
                } else {
                    statusLabel.setText("映像を待っています...");
                    VideoPlayer.this.start();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        panel.add(this.imageRenderer, BorderLayout.CENTER);
        this.setSize(1280, 750);
        this.setTitle("ATOMCamClient");
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        Runnable renderLoop = new Runnable() {
            public void run() {
                if (!imageRenderer.isValid())
                    return;
                imageRenderer.render();
                SwingUtilities.invokeLater(this);
            }
        };
        SwingUtilities.invokeLater(renderLoop);
    }

    public void start() {
        if (!stopGetVideoThread) return;
        this.stopGetVideoThread = false;
        this.getVideoThread.start();
    }

    public void stop() {
        stopGetVideoThread = true;
    }
}
