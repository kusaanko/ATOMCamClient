package com.github.kusaanko.atomcamclient.api.response;

import com.github.kusaanko.atomcamclient.api.device.ATOMDevice;

import java.util.List;

public class ATOMApiDeviceListResponse extends ATOMApiResponse {
    private List<ATOMDevice> devices;

    public List<ATOMDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<ATOMDevice> devices) {
        this.devices = devices;
    }
}
