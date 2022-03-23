package com.github.kusaanko.atomcamclient.api.p2p.command;

// com.HLApi.CameraAPI.protocol.CommandInfo
public class CommandInfo {
    private int requestCode;
    private byte[] data;
    private String p2pID;
    private String mac;
    private int sessionHandleID;

    public CommandInfo(int requestCode, byte[] data, String p2pID, String mac) {
        this.requestCode = requestCode;
        this.data = data;
        this.p2pID = p2pID;
        this.mac = mac;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getP2pID() {
        return p2pID;
    }

    public void setP2pID(String p2pID) {
        this.p2pID = p2pID;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getSessionHandleID() {
        return sessionHandleID;
    }

    public void setSessionHandleID(int sessionHandleID) {
        this.sessionHandleID = sessionHandleID;
    }
}
