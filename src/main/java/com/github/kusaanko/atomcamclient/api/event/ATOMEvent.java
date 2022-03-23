package com.github.kusaanko.atomcamclient.api.event;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ATOMEvent {
    @SerializedName("device_mac")
    private String deviceMac;
    @SerializedName("event_id")
    private String event_id;
    @SerializedName("device_model")
    private String deviceModel;
    @SerializedName("read_state")
    private String readState;
    @SerializedName("file_list")
    private List<ATOMEventFile> fileList;
    @SerializedName("tag_list")
    private List<Object> tagList;
    @SerializedName("event_ts")
    private long eventTs;
    @SerializedName("event_ack_result")
    private int eventAckResult;
    @SerializedName("event_category")
    private int eventCategory;
    @SerializedName("event_value")
    private String eventValue;
    @SerializedName("event_params")
    private Map<String, Object> eventParams;

    public String getDeviceMac() {
        return deviceMac;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getReadState() {
        return readState;
    }

    public List<ATOMEventFile> getFileList() {
        return fileList;
    }

    public List<Object> getTagList() {
        return tagList;
    }

    public long getEventTs() {
        return eventTs;
    }

    public int getEventAckResult() {
        return eventAckResult;
    }

    public int getEventCategory() {
        return eventCategory;
    }

    public String getEventValue() {
        return eventValue;
    }

    public Map<String, Object> getEventParams() {
        return eventParams;
    }

    @Override
    public String toString() {
        return "ATOMEvent{" +
                "deviceMac='" + deviceMac + '\'' +
                ", event_id='" + event_id + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", eventTs=" + eventTs +
                '}';
    }
}
