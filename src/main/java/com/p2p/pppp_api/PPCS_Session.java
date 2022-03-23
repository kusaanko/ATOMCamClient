package com.p2p.pppp_api;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class PPCS_Session extends Structure {
    public int Skt;
    public byte[] RemoteIP = new byte[16];
    //public int RemotePort = 0;
    public byte[] MyLocalIP = new byte[16];
    //public int MyLocalPort = 0;
    public byte[] MyWanIP = new byte[16];
    //public int MyWanPort = 0;
    public int ConnectTime = 0;
    public byte[] DID = new byte[24];
    public byte bCorD = 0;
    public byte bMode = 0;
    public byte[] Reversed = new byte[2];

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("Skt", "RemoteIP", "MyLocalIP", "MyWanIP", "ConnectTime", "DID", "bCorD", "bMode", "Reversed");
    }

    public int getMode() {
        return bMode & 255;
    }

    public String getRemoteIP() {
        return (this.RemoteIP[4] & 0xFF) + "." + (this.RemoteIP[5] & 0xFF) + "." + (this.RemoteIP[6] & 0xFF) + "." + (this.RemoteIP[7] & 0xFF);
    }

    public int getRemotePort() {
        return this.RemoteIP[2] << 8 + this.RemoteIP[1];
    }
}
