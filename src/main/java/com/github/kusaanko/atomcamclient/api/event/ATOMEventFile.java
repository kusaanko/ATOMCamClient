package com.github.kusaanko.atomcamclient.api.event;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ATOMEventFile {
    @SerializedName("is_ai")
    private int isAI;
    @SerializedName("ai_url")
    private String aiURL;
    @SerializedName("file_params")
    private Map<String, Object> fileParams;
    @SerializedName("en_password")
    private String enPassword;
    private int type;
    private String url;
    @SerializedName("device_mac")
    private String deviceMac;
    @SerializedName("event_id")
    private String eventID;
    @SerializedName("en_algorithm")
    private int enAlgorithm;
    @SerializedName("file_id")
    private String fileID;
    @SerializedName("event_ts")
    private long eventTs;
    @SerializedName("ai_tag_list")
    private List<String> aiTagList;
    private int status;

    public int getIsAI() {
        return isAI;
    }

    public String getAiURL() {
        return aiURL;
    }

    public Map<String, Object> getFileParams() {
        return fileParams;
    }

    public String getEnPassword() {
        return enPassword;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public String getEventID() {
        return eventID;
    }

    public int getEnAlgorithm() {
        return enAlgorithm;
    }

    public String getFileID() {
        return fileID;
    }

    public long getEventTs() {
        return eventTs;
    }

    public List<String> getAiTagList() {
        return aiTagList;
    }

    public int getStatus() {
        return status;
    }
}
