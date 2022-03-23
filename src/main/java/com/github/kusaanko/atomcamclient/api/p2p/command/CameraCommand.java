package com.github.kusaanko.atomcamclient.api.p2p.command;

import com.github.kusaanko.atomcamclient.api.util.XXTea;
import org.json.JSONObject;

import java.util.HashMap;

public class CameraCommand {
    public static enum MediaType {
        VIDEO,
        AUDIO,
        SPEAK,
    }

    public static byte[] setVideoParams(int bitRatio, int resolution, byte[] data) {
        HashMap<String, String> params = new HashMap<>();
        if (bitRatio <= 30) bitRatio = 30;
        else if (bitRatio <= 60) bitRatio = 60;
        else bitRatio = 120;
        params.put(CommandNumberContact.NUM_BIT, String.valueOf(bitRatio));
        if (resolution != 0) {
            params.put(CommandNumberContact.NUM_RATIO, String.valueOf(resolution));
        }
        return CommandManager.createCmdBytes(params, data);
    }

    public static byte[] connectRequest() {
        CommandParamsData data = new CommandParamsData();
        data.setEventCode(1);
        data.setParam("Type", "1");
        return CommandManager.getRandomKey(data);
    }

    public static byte[] enableMedia(MediaType mediaType, boolean isEnableMedia, byte[] data) {
        CommandParamsData paramsData = new CommandParamsData();
        paramsData.setEventCode(3);
        paramsData.setKey(data);
        String mediaTypeString;
        switch (mediaType) {
            case AUDIO:
                mediaTypeString = CommandNumberContact.NUM_AUDIO_CHANNEL;
                break;
            case SPEAK:
                mediaTypeString = CommandNumberContact.NUM_SPEAK_CHANNEL;
                break;
            case VIDEO:
            default:
                mediaTypeString = CommandNumberContact.NUM_VIDEO_CHANNEL;
        }
        paramsData.setParam(mediaTypeString, isEnableMedia ? "1" : "2");
        return CommandManager.setProperty(paramsData);
    }

    public static byte[] requestPlayback(boolean isStartPlayback, int mediaType, long timestamp, int speed, byte[] data) {
        if (!isStartPlayback) {
            return CommandManager.createCommand(RequestCode.getRequestCode(5), null, 0);
        }
        JSONObject json = new JSONObject();
        json.put("Ts", (int) timestamp);
        json.put("Speed", speed);
        byte[] js = XXTea.Encrypt(json.toString().getBytes(), data);
        return CommandManager.createCommand(RequestCode.getRequestCode(4), js, js.length);
    }
}
