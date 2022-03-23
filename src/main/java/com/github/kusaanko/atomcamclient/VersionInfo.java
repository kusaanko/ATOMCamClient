package com.github.kusaanko.atomcamclient;

import javax.swing.*;

public class VersionInfo extends JDialog {
    public VersionInfo(JFrame parent) {
        super(parent);
        this.add(new JLabel("ATOMCamClient - " + Main.version, JLabel.CENTER));
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setTitle("ATOMCamClient - バージョン情報");
        this.setSize(300, 300);
        this.setLocationRelativeTo(null);
        this.setModal(true);
        this.setVisible(true);
    }
}
