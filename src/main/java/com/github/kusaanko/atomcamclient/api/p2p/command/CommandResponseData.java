package com.github.kusaanko.atomcamclient.api.p2p.command;

public class CommandResponseData {
    private final int responseCode;
    private final byte[] data;

    public CommandResponseData(int responseCode, byte[] data) {
        this.responseCode = responseCode;
        this.data = data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public byte[] getData() {
        return data;
    }
}
