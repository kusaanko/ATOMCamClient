package com.github.kusaanko.atomcamclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CameraWindow extends JFrame {
    public CameraWindow() {
        CameraPanel cameraPanel = new CameraPanel(this);
        this.setLayout(new BorderLayout());
        this.add(cameraPanel, BorderLayout.CENTER);
        this.setTitle("ATOMCamClient");
        this.setSize(500, 500);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cameraPanel.start();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cameraPanel.close();
            }
        });
    }
}
