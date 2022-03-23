package com.github.kusaanko.atomcamclient;

import com.github.kusaanko.atomcamclient.api.ATOMApi;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    public LoginWindow() {
        super();
        this.setLayout(null);
        JLabel atomLabel = new JLabel("ATOMCamClient");
        atomLabel.setFont(atomLabel.getFont().deriveFont(20f));
        atomLabel.setForeground(new Color(236, 97, 46));
        atomLabel.setBounds(10, 10, 300, 30);
        this.add(atomLabel);
        JLabel userLabel = new JLabel("ユーザー名");
        userLabel.setFont(userLabel.getFont().deriveFont(15f));
        userLabel.setBounds(10, 80, 100, 20);
        this.add(userLabel);
        JTextField userField = new JTextField();
        userField.setFont(userField.getFont().deriveFont(16f));
        userField.setBounds(10, 100, 280, 25);
        this.add(userField);
        JLabel passwordLabel = new JLabel("パスワード");
        passwordLabel.setFont(passwordLabel.getFont().deriveFont(15f));
        passwordLabel.setBounds(10, 130, 100, 20);
        this.add(passwordLabel);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(passwordField.getFont().deriveFont(16f));
        passwordField.setBounds(10, 150, 280, 25);
        this.add(passwordField);
        JButton loginButton = new JButton("ログイン");
        loginButton.setFont(loginButton.getFont().deriveFont(18f));
        loginButton.setBounds(30, 200, 280, 30);
        loginButton.addActionListener(e -> {
            VerifyUserWindow verifyUserWindow = new VerifyUserWindow(userField.getText(), new String(passwordField.getPassword()));
            ATOMApi.login(userField.getText(), new String(passwordField.getPassword()));
            this.dispose();
        });
        this.add(loginButton);
        this.setSize(350, 400);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("ATOMにログイン");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
