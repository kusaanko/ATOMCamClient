package com.github.kusaanko.atomcamclient;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater extends JFrame {
    private final JProgressBar progressBar;
    private final JLabel statusLabel;

    public Updater(JSONObject json) {
        super();
        this.progressBar = new JProgressBar();
        this.progressBar.setMaximum(100);
        this.progressBar.setMinimum(0);
        this.progressBar.setValue(0);
        this.statusLabel = new JLabel("github.comに接続中...");
        new Thread(() -> {
            JSONObject assetJson = json.getJSONArray("assets").getJSONObject(0);
            String url = assetJson.getString("browser_download_url");
            String name = assetJson.getString("name");
            long size = assetJson.getLong("size");
            Path temp = Paths.get("temp");
            Path downloadFile = Paths.get(temp.toString(), name);
            try {
                Files.createDirectories(temp);
                byte[] buff = new byte[8192];
                int len;
                URL u = new URL(url);
                HttpsURLConnection connection = (HttpsURLConnection) u.openConnection();
                connection.setRequestProperty("User-Agent", "ATOMCamClient");
                connection.connect();
                if (connection.getResponseCode() != 200) {
                    statusLabel.setText("接続に失敗しました");
                    return;
                }
                try (InputStream inputStream = connection.getInputStream(); OutputStream outputStream = Files.newOutputStream(downloadFile)) {
                    int downloaded = 0;
                    while ((len = inputStream.read(buff)) != -1) {
                        outputStream.write(buff, 0, len);
                        downloaded += len;
                        statusLabel.setText("アップデートをダウンロード中... " + (downloaded / 1024 / 1024) + "MiB / " + (size / 1024 / 1024) + "MiB");
                        progressBar.setValue((int) ((float) downloaded / size * 100));
                    }
                    statusLabel.setText("解凍中...");
                }
                try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(downloadFile))) {
                    while (zipInputStream.available() > 0) {
                        ZipEntry zipEntry = zipInputStream.getNextEntry();
                        if (zipEntry != null) {
                            Path file = Paths.get(zipEntry.getName());
                            if (zipEntry.isDirectory()) {
                                Files.createDirectories(file);
                            } else {
                                try (OutputStream outputStream = Files.newOutputStream(file)) {
                                    while ((len = zipInputStream.read(buff)) != -1) {
                                        outputStream.write(buff, 0, len);
                                    }
                                }
                            }
                        }
                    }
                }
                this.deleteDirectory(temp);
                statusLabel.setText("再起動中...");
                restartApplication();
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }).start();
        this.setLayout(new BorderLayout());
        this.add(this.progressBar, BorderLayout.CENTER);
        this.add(this.statusLabel, BorderLayout.SOUTH);
        this.setSize(300, 100);
        this.setLocationRelativeTo(null);
        this.setTitle("ATOMCamClient - アップデート中");
        this.setVisible(true);
    }

    public void restartApplication() throws IOException, URISyntaxException {
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        Path currentJar = Paths.get(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if (!currentJar.getFileName().toString().endsWith(".jar")) {
            System.exit(0);
            return;
        }

        ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.toString());

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }

    private void deleteDirectory(Path dir) throws IOException {
        Files.list(dir).forEach(path -> {
            try {
                if (Files.isDirectory(path)) {
                    deleteDirectory(path);
                } else {
                    Files.delete(path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Files.delete(dir);
    }
}
