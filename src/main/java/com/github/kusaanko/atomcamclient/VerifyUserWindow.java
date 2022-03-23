package com.github.kusaanko.atomcamclient;

import com.github.kusaanko.atomcamclient.api.ATOMApi;

import javax.swing.*;
import java.awt.*;

public class VerifyUserWindow extends JFrame {
    public VerifyUserWindow(String userName, String password) {
        super();
        this.setLayout(null);
        JLabel atomLabel = new JLabel("ATOMCamClient");
        atomLabel.setFont(atomLabel.getFont().deriveFont(20f));
        atomLabel.setForeground(new Color(236, 97, 46));
        atomLabel.setBounds(10, 10, 300, 30);
        this.add(atomLabel);
        JLabel verifyCodeLabel = new JLabel("認証コード");
        verifyCodeLabel.setFont(verifyCodeLabel.getFont().deriveFont(15f));
        verifyCodeLabel.setBounds(10, 80, 100, 20);
        this.add(verifyCodeLabel);
        JTextField verifyCodeField = new JTextField();
        verifyCodeField.setFont(verifyCodeField.getFont().deriveFont(16f));
        verifyCodeField.setBounds(10, 100, 280, 25);
        this.add(verifyCodeField);
        JButton verifyButton = new JButton("認証");
        verifyButton.setFont(verifyButton.getFont().deriveFont(18f));
        verifyButton.setBounds(200, 150, 100, 30);
        verifyButton.addActionListener(e -> {
            if (verifyCodeField.getText() != null && !verifyCodeField.getText().isEmpty()) {
                ATOMApi.login(userName, password, verifyCodeField.getText());
                this.dispose();
                MainWindow.create();
            } else {
                JOptionPane.showMessageDialog(this, "認証コードを入力してください", "ATOMCamClient", JOptionPane.ERROR_MESSAGE);
            }
        });
        this.add(verifyButton);
        JButton skipButton = new JButton("認証をスキップ");
        skipButton.setFont(skipButton.getFont().deriveFont(13f));
        skipButton.setBounds(170, 190, 150, 25);
        skipButton.addActionListener(e -> {
            this.dispose();
            MainWindow.create();
        });
        this.add(skipButton);
        JButton resendButton = new JButton("認証コードを再送信");
        resendButton.setFont(resendButton.getFont().deriveFont(13f));
        resendButton.setBounds(20, 150, 170, 30);
        resendButton.addActionListener(e -> {
            ATOMApi.getVerifyCode(userName);
        });
        this.add(resendButton);
        this.setSize(350, 350);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("ATOMにログイン");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
