package com.github.kusaanko.atomcamclient.api.p2p.command;

import java.util.HashMap;
import java.util.Map;

public class RequestCode {
    public static final int REQUEST_GET_RANDOM_KEY = 1;
    public static final int REQUEST_QUERY_PROPERTY_LIST = 2;
    public static final int REQUEST_SET_PROPERTY_LIST = 3;
    public static final int REQUEST_PLAYBACK_START = 4;
    public static final int REQUEST_PLAYBACK_STOP = 5;
    public static final int REQUEST_PLAY_BACK_INFO = 6;
    public static final int REQUEST_FORMAT_MEMERY_CARD = 7;
    public static final int REQUEST_TIMELAPSE_STATUS = 8;
    public static final int REQUEST_TIMELAPSE_SET = 9;
    public static final int REQUEST_VIDEO_LIST = 10;
    public static final int REQUEST_VIDEO_DELETE = 11;
    public static final int REQUEST_FIRWARE_UPDATE = 12;
    public static final int REQUEST_PLAYBACK_TIME = 13;
    public static final int REQUEST_BOA_START = 14;
    public static final int REQUEST_MULTIPLE_ALARM_PERIOD = 15;
    public static final int REQUEST_SET_MULTIPLE_ALARM_PERIOD = 16;
    public static final int REQUEST_DELETE_ALARM_PERIOD = 17;
    public static final int REQUEST_NAS_SEARCH_DEVICES = 18;
    public static final int REQUEST_NAS_VERIFY_OR_SWITCH_NAS_DEVICES = 19;
    public static final int REQUEST_NAS_SEARCH_SHARE_FILE = 20;
    public static final int REQUEST_NAS_SEARCH_SHARE_CHILD_FILE = 21;
    public static final int REQUEST_DOWNLOAD_FILE_PATH = 22;
    public static final int REQUEST_DOWNLOAD_FILE_DATE = 23;
    public static final int REQUEST_RELATIVE_ROTATE = 24;
    public static final int REQUEST_CONTINUOUS_ROTATE = 25;
    public static final int REQUEST_GET_CRUISE_POINT = 26;
    public static final int REQUEST_SET_CRUISE_LOCATIONS = 27;
    public static final int REQUEST_GET_CRUISE_LOCATIONS = 28;
    public static final int REQUEST_SET_PTZ_TO_POINT = 29;
    public static final int REQUEST_PTZ_RESET = 30;
    public static final int RESPONSE_ROTARY_LIMIT = 31;
    public static final int RESPONSE_DEFAULT_CRUISE_LOCATION = 32;
    public static final int REQUEST_GET_AI_DETECTION_SWITCH_STATUS = 33;
    public static final int REQUEST_SET_AI_DETECTION_SWITCH_STATUS = 34;
    public static final int REQUEST_GET_RTSP_INFO = 35;
    public static final int REQUEST_SET_RTSP_ACTION = 36;
    public static final Map<Integer, Integer> requestCodeMap;

    static {
        requestCodeMap = new HashMap<>();
        requestCodeMap.put(1, 10);
        requestCodeMap.put(2, 66);
        requestCodeMap.put(3, 38);
        requestCodeMap.put(4, 54);
        requestCodeMap.put(5, 88);
        requestCodeMap.put(6, 76);
        requestCodeMap.put(7, 96);
        requestCodeMap.put(8, 16);
        requestCodeMap.put(9, 14);
        requestCodeMap.put(10, 58);
        requestCodeMap.put(11, 78);
        requestCodeMap.put(12, 68);
        requestCodeMap.put(13, 80);
        requestCodeMap.put(14, 94);
        requestCodeMap.put(15, 116);
        requestCodeMap.put(16, 98);
        requestCodeMap.put(17, 228);
        requestCodeMap.put(18, 126);
        requestCodeMap.put(19, 136);
        requestCodeMap.put(20, 188);
        requestCodeMap.put(21, 160);
        requestCodeMap.put(22, 230);
        requestCodeMap.put(23, 176);
        requestCodeMap.put(24, 84);
        requestCodeMap.put(25, 32);
        requestCodeMap.put(26, 74);
        requestCodeMap.put(27, 60);
        requestCodeMap.put(28, 74);
        requestCodeMap.put(29, 270);
        requestCodeMap.put(30, 56);
        requestCodeMap.put(31, 62);
        requestCodeMap.put(32, 248);
        requestCodeMap.put(33, 530);
        requestCodeMap.put(34, 398);
        requestCodeMap.put(35, 222);
        requestCodeMap.put(36, 104);
    }

    public static int getRequestCode(int eventCode) {
        return requestCodeMap.get(eventCode);
    }

}
