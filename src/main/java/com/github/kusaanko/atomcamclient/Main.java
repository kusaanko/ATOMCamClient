package com.github.kusaanko.atomcamclient;

import com.github.kusaanko.atomcamclient.api.ATOMApi;
import com.github.kusaanko.atomcamclient.api.response.ATOMApiResponse;
import com.github.kusaanko.atomcamclient.api.util.DataManager;
import org.json.JSONArray;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Objects;

public class Main {
    public static final String version = "0.2.0";
    public static final String tagName = "v0.2.0";
    public static final String repositoryReleasesURL = "https://api.github.com/repos/kusaanko/ATOMCamClient/releases";

    public static void main(String[] args) throws IOException {
        // For macOS
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ATOMCamClient");
        try {
            DataManager.getInstance().load(Files.newInputStream(Paths.get("data")));
        } catch (NoSuchFileException ignore) {
        }

        // Check updates
        new Thread(() -> {
            try {
                JSONArray releases = isUpdateAvailable();
                if (releases != null) {
                    new UpdateNotifier(releases);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        if (ATOMApi.isSetDeviceInfoNeeded()) {
            ATOMApi.setDeviceInfo();
        }
        do {
            if (ATOMApi.isLoginNeeded()) {
                LoginWindow loginWindow = new LoginWindow();
                break;
            } else {
                ATOMApiResponse response = ATOMApi.refreshToken();
                if (Objects.equals(response.getCode(), ATOMApiResponse.SUCCESS)) {
                    MainWindow.create();
                    break;
                }
                ATOMApi.clearLoginData();
            }
        } while (ATOMApi.isLoginNeeded());

        // Get event list
        /*
        ATOMApiDeviceListResponse deviceListResponse = ATOMApi.getDeviceList();
        List<ATOMDevice> devices = deviceListResponse.getDevices();
        if(Objects.equals(deviceListResponse.getCode(), ATOMApiResponse.SUCCESS)) for(ATOMDevice device : devices) {
            System.out.println(device.getNickname());
            long lastTimestamp = System.currentTimeMillis();
            for(int i = 0;i < 1;i++) {
                List<ATOMEvent> events = ATOMApi.getEventList(device.getMac(), lastTimestamp - 1000 * 60 * 60 * 24, lastTimestamp, 20);
                SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm:ss");
                for(ATOMEvent event : events) {
                    if(event.getFileList().size() == 2) {
                        System.out.println(format.format(new Date(event.getEventTs())) + " " +
                                (event.getFileList().get(1).getIsAI() == 1 ? event.getFileList().get(1).getAiTagList().get(0) : "motion") +
                                " movie url:" + event.getFileList().get(1).getUrl());
                    }
                }
                lastTimestamp = events.get(events.size() - 1).getEventTs();
            }
        }*/
    }

    public static JSONArray isUpdateAvailable() throws IOException {
        URL url = new URL(repositoryReleasesURL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "ATOMCamClient");
        connection.connect();
        if (connection.getResponseCode() == 200) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (InputStream inputStream = connection.getInputStream()) {
                    byte[] buff = new byte[8192];
                    int len;
                    while ((len = inputStream.read(buff)) != -1) {
                        baos.write(buff, 0, len);
                    }
                }
                JSONArray releases = new JSONArray(baos.toString(String.valueOf(StandardCharsets.UTF_8)));
                if (releases.length() > 0) {
                    String tag = releases.getJSONObject(0).getString("tag_name");
                    if (!Objects.equals(tag, tagName)) {
                        return releases;
                    }
                }
            }
        }
        return null;
    }
}
