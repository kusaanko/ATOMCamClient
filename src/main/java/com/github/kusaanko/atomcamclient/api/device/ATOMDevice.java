package com.github.kusaanko.atomcamclient.api.device;

import com.github.kusaanko.atomcamclient.api.device.params.ATOMCam2Params;
import com.github.kusaanko.atomcamclient.api.device.params.ATOMCamParams;
import com.github.kusaanko.atomcamclient.api.device.params.ATOMCamSwingParams;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ATOMDevice {
    private final String firmwareVer;
    private final String productDisplayname;
    private final long firstBindingTs;
    private final long isInAuto;// Auto means car
    private final String mac;
    private final int userRole;
    private final String parentDeviceMac;
    private final int connState;// Connection status
    private final List<String> deviceCapacityList;
    private final String nickname;
    private final String timezoneName;
    private final String productModelLogoUrl;
    private final long firstActivationTs;
    private final long connStateTs;
    private final String productModel;
    private final String bindingUserNickname;
    private final String hardwareVer;
    private final String enr;
    private final int timezoneGmtOffset;// example GMT+9
    private final String productType;
    private final long bindingTs;
    private final DeviceParams deviceParams;
    private final int pushSwitch;
    private int eventMasterSwitch;
    private final String parentDeviceEnr;

    public static final String modelAtomDongle = "UD3J";
    public static final String modelAtomMotionSensor = "PIR3J";
    public static final String modelAtomContact = "DWS3J";
    public static final String modelAtomCam = "AC1";
    public static final String modelAtomCam2 = "ATOM_CamV3C";
    public static final String modelAtomCamSwing = "ATOM_CAKP1JZJP";

    public ATOMDevice(JSONObject json) {
        this.firmwareVer = json.getString("firmware_ver");
        this.productDisplayname = json.getString("product_displayname");
        this.firstBindingTs = json.getLong("first_binding_ts");
        this.isInAuto = json.getInt("is_in_auto");
        this.mac = json.getString("mac");
        this.userRole = json.getInt("user_role");
        this.parentDeviceMac = json.getString("parent_device_mac");
        this.connState = json.getInt("conn_state");
        if (!json.isNull("device_capacity_list")) {
            this.deviceCapacityList = new ArrayList<>();
            JSONArray list = json.getJSONArray("device_capacity_list");
            for (int i = 0; i < list.length(); i++) {
                this.deviceCapacityList.add(list.getString(i));
            }
        } else {
            this.deviceCapacityList = new ArrayList<>();
        }
        this.nickname = json.getString("nickname");
        this.timezoneName = json.getString("timezone_name");
        this.productModelLogoUrl = json.getString("product_model_logo_url");
        this.firstActivationTs = json.getLong("first_activation_ts");
        this.connStateTs = json.getLong("conn_state_ts");
        this.productModel = json.getString("product_model");
        this.bindingUserNickname = json.getString("binding_user_nickname");
        this.hardwareVer = json.getString("hardware_ver");
        this.enr = json.getString("enr");
        this.timezoneGmtOffset = json.getInt("timezone_gmt_offset");
        this.productType = json.getString("product_type");
        this.bindingTs = json.getLong("binding_ts");
        this.pushSwitch = json.getInt("push_switch");
        if (json.has("event_master_switch")) {
            this.eventMasterSwitch = json.getInt("event_master_switch");
        }
        this.parentDeviceEnr = json.getString("parent_device_enr");
        Gson gson = new Gson();
        switch (this.getProductModel()) {
            case modelAtomCam:
                this.deviceParams = gson.fromJson(json.getJSONObject("device_params").toString(), ATOMCamParams.class);
                break;
            case modelAtomCam2:
                this.deviceParams = gson.fromJson(json.getJSONObject("device_params").toString(), ATOMCam2Params.class);
                break;
            case modelAtomCamSwing:
                this.deviceParams = gson.fromJson(json.getJSONObject("device_params").toString(), ATOMCamSwingParams.class);
                break;
            default:
                this.deviceParams = new DeviceParams();
        }
    }

    public boolean isCamera() {
        switch (this.getProductModel()) {
            case modelAtomCam:
            case modelAtomCam2:
            case modelAtomCamSwing:
                return true;
            default:
                return false;
        }
    }

    public String getFirmwareVer() {
        return firmwareVer;
    }

    public String getProductDisplayname() {
        return productDisplayname;
    }

    public long getFirstBindingTs() {
        return firstBindingTs;
    }

    public long getIsInAuto() {
        return isInAuto;
    }

    public String getMac() {
        return mac;
    }

    public int getUserRole() {
        return userRole;
    }

    public String getParentDeviceMac() {
        return parentDeviceMac;
    }

    public int getConnState() {
        return connState;
    }

    public List<String> getDeviceCapacityList() {
        return deviceCapacityList;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTimezoneName() {
        return timezoneName;
    }

    public String getProductModelLogoUrl() {
        return productModelLogoUrl;
    }

    public long getFirstActivationTs() {
        return firstActivationTs;
    }

    public long getConnStateTs() {
        return connStateTs;
    }

    public String getProductModel() {
        return productModel;
    }

    public String getBindingUserNickname() {
        return bindingUserNickname;
    }

    public String getHardwareVer() {
        return hardwareVer;
    }

    public String getEnr() {
        return enr;
    }

    public int getTimezoneGmtOffset() {
        return timezoneGmtOffset;
    }

    public String getProductType() {
        return productType;
    }

    public long getBindingTs() {
        return bindingTs;
    }

    public DeviceParams getDeviceParams() {
        return deviceParams;
    }

    public int getPushSwitch() {
        return pushSwitch;
    }

    public int getEventMasterSwitch() {
        return eventMasterSwitch;
    }

    public String getParentDeviceEnr() {
        return parentDeviceEnr;
    }
}
