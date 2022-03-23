package com.p2p.pppp_api;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface PPCS_API extends Library {
    PPCS_API INSTANCE = Native.load("PPCS_API", PPCS_API.class);

    int PPCS_GetAPIVersion();

    int PPCS_Initialize(byte[] Parameter);

    int PPCS_ConnectByServer(String TargetID, byte bEnableLanSearch, char UDP_Port, String ServerString);

    //int PPCS_Check(int SessionHandle, PPCS_Session SInfo);
    int PPCS_Write(int SessionHandle, byte Channel, byte[] DataBuf, int DataSizeToWrite);

    int PPCS_Read(int SessionHandle, byte Channel, byte[] DataBuf, int[] DataSize, long TimeOut_ms);

    int PPCS_DeInitialize();

    int PPCS_Close(int SessionHandle);

    int PPCS_ForceClose(int SessionHandle);
}
