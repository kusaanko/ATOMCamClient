package com.github.kusaanko.atomcamclient.api.p2p.command;

import java.util.HashMap;
import java.util.Map;

// com.p009hl.camera.connect.CMD.bean.CmdParamsBean
public class CommandParamsData {
    private int eventCode;
    private byte[] key;
    private Map<String, String> paramMap;

    public CommandParamsData() {
        this.paramMap = new HashMap<>();
    }

    public CommandParamsData(int eventCode, byte[] key) {
        this.paramMap = new HashMap<>();
        this.eventCode = eventCode;
        this.key = key;
    }

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public void setParam(String key, String value) {
        this.paramMap.put(key, value);
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }
}
