package com.github.kusaanko.atomcamclient;

import com.github.kusaanko.atomcamclient.api.ATOMApi;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MainWindow extends JFrame {
    private static MainWindow instance;
    private JMenuBar menuBar;

    public static void create() {
        instance = new MainWindow();
    }

    public MainWindow() {
        this.menuBar = new JMenuBar();
        this.setJMenuBar(this.menuBar);
        {
            JMenu filesMenu = new JMenu("ファイル");
            JMenuItem newWindowMenu = new JMenuItem("新規ウィンドウ");
            newWindowMenu.addActionListener(e -> {
                new CameraWindow();
            });
            filesMenu.add(newWindowMenu);
            JMenuItem logoutMenu = new JMenuItem("ログアウト");
            logoutMenu.addActionListener(e -> {
                this.dispose();
                ATOMApi.logout();
                this.dispose();
                System.exit(0);
            });
            filesMenu.add(logoutMenu);
            this.menuBar.add(filesMenu);
        }
        {
            JMenu otherMenu = new JMenu("その他");
            JMenuItem versionInfoMenu = new JMenuItem("バージョン情報");
            versionInfoMenu.addActionListener(e -> {
                new VersionInfo(this);
            });
            otherMenu.add(versionInfoMenu);
            JMenuItem checkUpdates = new JMenuItem("アップデートを確認");
            checkUpdates.addActionListener(e -> {
                try {
                    JSONArray releases = Main.isUpdateAvailable();
                    if (releases != null) {
                        new UpdateNotifier(releases);
                    } else {
                        JOptionPane.showMessageDialog(this, "アップデートはありません。");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            otherMenu.add(checkUpdates);
            this.menuBar.add(otherMenu);
        }
        this.setLayout(new BorderLayout());
        CameraPanel cameraPanel = new CameraPanel(this);
        this.add(cameraPanel, BorderLayout.CENTER);
        this.setTitle("ATOMCamClient");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(900, 700);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        cameraPanel.start();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cameraPanel.close();
            }
        });
    }
}
