package com.github.kusaanko.atomcamclient.api;

import com.github.kusaanko.atomcamclient.api.device.ATOMDevice;
import com.github.kusaanko.atomcamclient.api.event.ATOMEvent;
import com.github.kusaanko.atomcamclient.api.response.ATOMApiDeviceListResponse;
import com.github.kusaanko.atomcamclient.api.response.ATOMApiResponse;
import com.github.kusaanko.atomcamclient.api.util.DataManager;
import com.github.kusaanko.atomcamclient.api.util.MD5;
import com.github.kusaanko.atomcamclient.api.util.PropertiesManager;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ATOMApi {
    private static final String endpoint;

    static {
        endpoint = PropertiesManager.getInstance().getADDR().getProperty("URL_BASE_OFFICIAL");
    }

    public static ATOMApiResponse setDeviceInfo() {
        String lang = "ja";
        if (!Locale.getDefault().getLanguage().equals(lang)) {
            lang = "en";
        }
        JSONObject json = generateDefaultJSONObject(PropertiesManager.getInstance().getSV().getProperty("SV_SET_PHONE_INFO"), System.currentTimeMillis());
        json.put("language", lang);
        json.put("phone_model", ATOMApp.buildModel.replace(" ", "_") + "_Android");
        json.put("system_type", ATOMApp.systemType);
        json.put("system_ver", "Android_" + ATOMApp.SDK_INT);
        int pushType = ATOMApp.isGooglePlayServicesAvailable ? 3 : 2;
        json.put("android_push_type", pushType);
        String pushToken = "45e0bd8552e0d15f7d1b76";
        if (!ATOMApp.isGooglePlayServicesAvailable) {
            pushToken = ATOMApp.bundleId;
        }
        json.put("app_num", pushToken);
        json.put("push_token", UUID.randomUUID().toString()); // TODO: push_token
        json.put("timezone_city", TimeZone.getDefault().getID());
        JSONObject body = request(PropertiesManager.getInstance().getURL().getProperty("URL_SET_PHONE_INFO"), json);
        return new Gson().fromJson(body.toString(), ATOMApiResponse.class);
    }

    public static ATOMApiResponse login(String name, String password) {
        return login(name, password, "");
    }

    public static ATOMApiResponse login(String name, String password, String verifyCode) {
        DataManager.getInstance().writeString("user_name", name);
        JSONObject json = generateDefaultJSONObject(PropertiesManager.getInstance().getSV().getProperty("SV_LOGIN"), System.currentTimeMillis());
        json.put("user_name", name);
        json.put("password", MD5.encode(MD5.encode(password)));
        json.put("verify_code", verifyCode);
        json.put("login_type", 1);
        JSONObject body = request(PropertiesManager.getInstance().getURL().getProperty("URL_LOGIN"), json);
        if (body.getString("code").equals("1")) {
            DataManager.getInstance().writeString("access_token", body.getJSONObject("data").getString("access_token"));
            DataManager.getInstance().writeString("refresh_token", body.getJSONObject("data").getString("refresh_token"));
        }
        return new Gson().fromJson(body.toString(), ATOMApiResponse.class);
    }

    public static ATOMApiResponse logout() {
        JSONObject json = generateDefaultJSONObject(PropertiesManager.getInstance().getSV().getProperty("SV_LOGOUT"), System.currentTimeMillis());
        JSONObject body = request(PropertiesManager.getInstance().getURL().getProperty("URL_LOGOUT"), json);
        if (body.getString("code").equals("1")) {
            clearLoginData();
        }
        return new Gson().fromJson(body.toString(), ATOMApiResponse.class);
    }

    public static void clearLoginData() {
        DataManager.getInstance().writeString("access_token", "");
        DataManager.getInstance().writeString("refresh_token", "");
    }

    public static ATOMApiResponse verifyUser(String email) {
        JSONObject json = generateDefaultJSONObject(PropertiesManager.getInstance().getSV().getProperty("SV_USER_VERIFY"), System.currentTimeMillis());
        json.put("user_email", email);
        JSONObject body = request(PropertiesManager.getInstance().getURL().getProperty("URL_USER_VERIFY"), json);
        if (body.getString("code").equals("1")) {
        }
        return new Gson().fromJson(body.toString(), ATOMApiResponse.class);
    }

    public static ATOMApiResponse getVerifyCode(String name) {
        JSONObject json = generateDefaultJSONObject(PropertiesManager.getInstance().getSV().getProperty("SV_SECURE_CODE"), System.currentTimeMillis());
        json.put("user_name", name);
        json.put("reg_type", "2");
        json.put("account_type", 2);
        JSONObject body = request(PropertiesManager.getInstance().getURL().getProperty("URL_SECURE_CODE"), json);
        if (body.getString("code").equals("1")) {
        }
        return new Gson().fromJson(body.toString(), ATOMApiResponse.class);
    }

    public static boolean isLoginNeeded() {
        return DataManager.getInstance().getString("access_token", "").isEmpty();
    }

    public static ATOMApiResponse refreshToken() {
        JSONObject json = generateDefaultJSONObject(PropertiesManager.getInstance().getSV().getProperty("SV_GET_TOKEN"), System.currentTimeMillis());
        json.put("refresh_token", DataManager.getInstance().getString("refresh_token", ""));
        JSONObject body = request(PropertiesManager.getInstance().getURL().getProperty("URL_GET_TOKEN"), json);
        if (body.getString("code").equals("1")) {
            DataManager.getInstance().writeString("access_token", body.getJSONObject("data").getString("access_token"));
        }
        return new Gson().fromJson(body.toString(), ATOMApiResponse.class);
    }

    public static ATOMApiDeviceListResponse getDeviceList() {
        JSONObject json = generateDefaultJSONObject(PropertiesManager.getInstance().getSV().getProperty("SV_HOME_PAGE_GET_OBJECT_LIST"), System.currentTimeMillis());
        JSONObject body = request(PropertiesManager.getInstance().getURL().getProperty("URL_HOME_PAGE_GET_OBJECT_LIST"), json);
        if (body.getString("code").equals("1")) {
            List<ATOMDevice> devices = new ArrayList<>();
            JSONArray deviceList = body.getJSONObject("data").getJSONArray("device_list");
            for (int iDeviceList = 0; iDeviceList < deviceList.length(); iDeviceList++) {
                JSONObject device = deviceList.getJSONObject(iDeviceList);
                ATOMDevice atomDevice = new ATOMDevice(device);
                devices.add(atomDevice);
            }
            ATOMApiDeviceListResponse deviceListResponse = new Gson().fromJson(body.toString(), ATOMApiDeviceListResponse.class);
            deviceListResponse.setDevices(devices);
            return deviceListResponse;
        }
        return new Gson().fromJson(body.toString(), ATOMApiDeviceListResponse.class);
    }

    public static JSONObject getV2DeviceInfo(String mac, String model) {
        JSONObject json = generateDefaultJSONObject(PropertiesManager.getInstance().getSV().getProperty("SV_GET_V2_DEVICE_INFO"), System.currentTimeMillis());
        json.put("device_mac", mac);
        json.put("device_model", model);
        JSONObject body = request(PropertiesManager.getInstance().getURL().getProperty("URL_GET_V2_DEVICE_INFO"), json);
        if (body.getString("code").equals("1")) {
            // TODO:
        }
        return body;
    }

    public static List<ATOMEvent> getEventList(String mac, long startMilli, long endMilli, long count) {
        JSONObject json = generateDefaultJSONObject(PropertiesManager.getInstance().getSV().getProperty("SV_GET_EVENT_LIST"), System.currentTimeMillis());
        json.put("device_mac_list", new JSONArray().put(mac));
        json.put("event_value_list", new JSONArray());
        json.put("event_tag_list", new JSONArray());
        json.put("event_type", "1");
        json.put("begin_time", startMilli);
        json.put("end_time", endMilli);
        json.put("count", count);// max 20
        json.put("order_by", 2);
        JSONObject body = request(PropertiesManager.getInstance().getURL().getProperty("URL_GET_EVENT_LIST"), json);
        if (body.getString("code").equals("1")) {
            JSONArray events = body.getJSONObject("data").getJSONArray("event_list");
            List<ATOMEvent> atomEvents = new ArrayList<>();
            Gson gson = new Gson();
            for (int i = 0; i < events.length(); i++) {
                atomEvents.add(gson.fromJson(events.getJSONObject(i).toString(), ATOMEvent.class));
            }
            return atomEvents;
        }
        return new ArrayList<>();
    }

    private static JSONObject request(String url, JSONObject content) {
        try {
            byte[] body = content.toString().getBytes();
            HttpsURLConnection connection = createConnection(endpoint + url);
            addHeaders(connection, content);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", String.valueOf(body.length));
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body);
            outputStream.flush();
            outputStream.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[8192];
            int len;
            while ((len = connection.getInputStream().read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            String response = baos.toString(StandardCharsets.UTF_8.toString());
            return new JSONObject(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private static JSONObject generateDefaultJSONObjectWithoutSV(long timestamp) {
        JSONObject json = new JSONObject();
        json.put("request_id", "1");
        json.put("timestamp", timestamp);
        json.put("terminal_id", getPhoneUUID());
        json.put("app_name", ATOMApp.bundleId);
        json.put("app_version", ATOMApp.version);
        json.put("os_name", ATOMApp.osName);
        json.put("os_version", ATOMApp.osVersion);
        return json;
    }

    private static JSONObject generateDefaultJSONObject(String sv, long timestamp) {
        JSONObject json = new JSONObject();
        json.put("sc", PropertiesManager.getInstance().getSV().getProperty("SC"));
        json.put("sv", sv);
        json.put("app_ver", ATOMApp.bundleId + "___" + ATOMApp.version);
        json.put("ts", timestamp);
        json.put("access_token", DataManager.getInstance().getString("access_token", ""));
        json.put("phone_id", getPhoneUUID());
        json.put("app_name", ATOMApp.bundleId);
        json.put("app_version", ATOMApp.version);
        json.put("phone_system_id", ATOMApp.systemType);
        return json;
    }

    public static String getPhoneUUID() {
        if (DataManager.getInstance().getString("phone_uuid", "").equals("")) {
            String uuid = UUID.randomUUID().toString();
            DataManager.getInstance().writeString("phone_uuid", uuid);
            return uuid;
        }
        return DataManager.getInstance().getString("phone_uuid", "");
    }

    public static boolean isSetDeviceInfoNeeded() {
        return DataManager.getInstance().getString("phone_uuid", "").isEmpty();
    }


    private static void addHeaders(HttpsURLConnection connection, JSONObject json) {
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(json.toString().getBytes().length));
    }

    private static HttpsURLConnection createConnection(String url) {
        try {
            URL u = new URL(url);
            return (HttpsURLConnection) u.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
