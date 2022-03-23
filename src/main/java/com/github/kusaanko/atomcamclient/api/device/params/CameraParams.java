package com.github.kusaanko.atomcamclient.api.device.params;

import com.github.kusaanko.atomcamclient.api.device.DeviceParams;

import java.util.List;

public class CameraParams extends DeviceParams {
    private int audio_alarm_switch;
    private String battery_charging_status;
    private int co_alarm_switch;
    private String electricity;
    private int motion_alarm_switch;
    private String p2p_id;
    private int p2p_type;
    private int power_switch;
    private int records_event_switch;
    private String scloud_address;
    private int smoke_alarm_switch;
    private String ssid;
    private List<String> supported_video_format;
    private int supportMultipleAlarmPeriod = 2;
    private String temperature = "";
    private String humidity = "";
    private int temp_humi_room_type = 0;
    private int comfort_standard_level = 0;
    private String is_temperature_humidity = "";

    public int getAudioAlarmSwitch() {
        return audio_alarm_switch;
    }

    public String getBatteryChargingStatus() {
        return battery_charging_status;
    }

    public int getCoAlarmSwitch() {
        return co_alarm_switch;
    }

    public String getElectricity() {
        return electricity;
    }

    public int getMotionAlarmSwitch() {
        return motion_alarm_switch;
    }

    public String getP2PId() {
        return p2p_id;
    }

    public String getFormattedP2PId() {
        if (this.getP2PId().contains(",")) {
            String str = this.getP2PId();
            return str.substring(0, str.indexOf(","));
        }
        return this.getP2PId();
    }

    public int getP2PType() {
        return p2p_type;
    }

    public int getPowerSwitch() {
        return power_switch;
    }

    public int getRecordsEventSwitch() {
        return records_event_switch;
    }

    public String getScloudAddress() {
        return scloud_address;
    }

    public int getSmokeAlarmSwitch() {
        return smoke_alarm_switch;
    }

    public String getSsid() {
        return ssid;
    }

    public List<String> getSupportedVideoFormat() {
        return supported_video_format;
    }

    public int getSupportMultipleAlarmPeriod() {
        return supportMultipleAlarmPeriod;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public int getTempHumiRoomType() {
        return temp_humi_room_type;
    }

    public int getComfortStandardLevel() {
        return comfort_standard_level;
    }

    public String getIsTemperatureHumidity() {
        return is_temperature_humidity;
    }
}
