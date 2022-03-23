package com.github.kusaanko.atomcamclient.api.p2p.command;

import com.github.kusaanko.atomcamclient.api.ATOMApi;
import com.github.kusaanko.atomcamclient.api.util.XXTea;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public class CommandManager {
    public static final int CMD_CONNECT_REQUEST = 10000;
    public static final int CMD_ENABLE_MEDIA = 10010;
    public static final int CMD_SET_VIDEO_PARAMS = 10056;
    public static final int CMD_REQUEST_PLAYBACK = 10450;

    public static final int CMD_HEADER_SIZE = 15;

    protected static final byte[] commandHeader1 = {72, 76};
    protected static final byte[] commandHeader2 = {72, 76, 75, 74};
    protected static byte[] commandHeader3 = {65, 90};

    public static CommandInfo createConnectRequest(String p2pID, String mac) {
        return new CommandInfo(CMD_CONNECT_REQUEST, CameraCommand.connectRequest(), p2pID, mac);
    }

    public static CommandInfo createEnableMedia(CameraCommand.MediaType mediaType, boolean isEnabled, byte[] xxTeaKey, String p2pID, String mac) {
        return new CommandInfo(CMD_ENABLE_MEDIA, CameraCommand.enableMedia(mediaType, isEnabled, xxTeaKey), p2pID, mac);
    }

    public static CommandInfo createSetVideoParams(int bitRatio, int resolution, byte[] xxTeaKey, String p2pID, String mac) {
        return new CommandInfo(CMD_SET_VIDEO_PARAMS, CameraCommand.setVideoParams(bitRatio, resolution, xxTeaKey), p2pID, mac);
    }

    public static CommandInfo createRequestPlayback(boolean isStartPlayback, int mediaType, long timestamp, int speed, byte[] xxTeaKey, String p2pID, String mac) {
        return new CommandInfo(CMD_REQUEST_PLAYBACK, CameraCommand.requestPlayback(isStartPlayback, mediaType, timestamp, speed, xxTeaKey), p2pID, mac);
    }

    public static byte[] createCmdBytes(Map<String, String> hashMap, byte[] data) {
        CommandParamsData commandParamsData = new CommandParamsData(3, data);
        commandParamsData.setParamMap(hashMap);
        return setProperty(commandParamsData);
    }

    public static byte[] setProperty(CommandParamsData commandParamsData) {
        return getCmdByte(RequestCode.getRequestCode(commandParamsData.getEventCode()), getCompleteSetCmdBody(commandParamsData.getParamMap()), commandParamsData.getKey());
    }

    private static String getCompleteSetCmdBody(Map<String, String> map) {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            json.put(entry.getKey(), Integer.parseInt(entry.getValue()));
        }
        JSONObject json2 = new JSONObject();
        json2.put("PropertyList", json);
        return json2.toString();
    }

    private static byte[] getCmdByte(int requestCode, String str, byte[] data) {
        byte[] encodeData;
        int length;
        if (!str.isEmpty()) {
            encodeData = str.getBytes();
        } else {
            encodeData = null;
        }
        byte[] encryptedData = XXTea.Encrypt(Objects.requireNonNull(encodeData), data);
        if (encryptedData == null) {
            length = 0;
        } else {
            length = encryptedData.length;
        }
        return createCommand(requestCode, encryptedData, length);
    }

    public static byte[] getRandomKey(CommandParamsData paramsData) {
        int requestCode = RequestCode.getRequestCode(paramsData.getEventCode());
        JSONObject json = new JSONObject();
        for (String key : paramsData.getParamMap().keySet()) {
            json.put(key, Integer.parseInt(paramsData.getParamMap().get(key)));
        }
        json.put("PhoneID", ATOMApi.getPhoneUUID().substring(0, 8));
        byte[] data = json.toString().getBytes();
        int len = data.length;
        return createCommand(requestCode, data, len);
    }

    public static byte[] createCommand(int requestCode, byte[] data, int length) {
        byte[] command = new byte[CMD_HEADER_SIZE + length];
        System.arraycopy(commandHeader1, 0, command, 0, 2);
        System.arraycopy(commandHeader2, 0, command, 2, 4);
        System.arraycopy(commandHeader3, 0, command, 6, 2);
        command[0] = 72;
        command[1] = 76;
        command[2] = 72;
        command[3] = 76;
        command[4] = 8;
        command[5] = 74;
        command[6] = 65;
        command[7] = 84;
        command[8] = 1;
        command[9] = (byte) (requestCode & 0xFF);
        command[10] = (byte) ((requestCode & 0xFF00) >> 8);
        command[11] = (byte) (length & 0xFF);
        command[12] = (byte) ((length & 0xFF00) >> 8);
        command[13] = (byte) ((length & 0xFF0000) >> 16);
        command[14] = (byte) ((length & 0xFF000000) >> 24);
        if (data != null) {
            System.arraycopy(data, 0, command, 15, length);
        }
        return command;
    }
}
