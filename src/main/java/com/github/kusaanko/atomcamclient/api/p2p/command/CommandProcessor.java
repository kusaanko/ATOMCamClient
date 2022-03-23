package com.github.kusaanko.atomcamclient.api.p2p.command;

import com.github.kusaanko.atomcamclient.api.av.P2PAv;
import com.github.kusaanko.atomcamclient.api.util.ATOMXXTea;
import com.github.kusaanko.atomcamclient.api.util.HLXXTea;

import java.util.Arrays;

public class CommandProcessor {
    private int dataSize;
    private int dataPosition;
    private byte[] tmpData;
    private byte[] processData;
    private final P2PAv p2PAv;
    private byte[] xxTeaKey;
    protected static final byte[] header = {72, 76};
    protected static final byte[] headerLocal = {73, 67, 65, 77};
    protected static final byte[] headerLocal2 = {72, 76, 65, 77};
    protected static final byte[] headerLog = {76, 79, 71, 84};

    public CommandProcessor(P2PAv p2PAv) {
        this.tmpData = new byte[1024];
        this.p2PAv = p2PAv;
    }

    private void responseConnectRequest(byte[] data) {
        if (data.length > 16) {
            byte[] rawXxTeaKey = Arrays.copyOfRange(data, 1, 17);
            if (data[0] == 1) {
                byte[] enr = changeEnrByteOrder(p2PAv.getEnr().substring(0, 16).getBytes());
                byte[] decryptData = changeEnrByteOrder(rawXxTeaKey);
                byte[] xxTeaKey = HLXXTea.decrypt(decryptData, enr);
                xxTeaKey = changeEnrByteOrder(xxTeaKey);
                this.p2PAv.setXxTeaKey(xxTeaKey);
                this.xxTeaKey = xxTeaKey;
                System.out.println("set xxtea key");
            }
        } else {
            //failed
            System.out.println("failed to decrypt xxtea key");
        }
    }

    private byte[] changeEnrByteOrder(byte[] enr) {
        byte[] newEnr = new byte[16];
        for (int i = 0; i < 4; i++) {
            newEnr[i] = enr[3 - i];
            newEnr[i + 4] = enr[7 - i];
            newEnr[i + 8] = enr[11 - i];
            newEnr[i + 12] = enr[15 - i];
        }
        return newEnr;
    }

    private void parseResponse(byte[] data) {
        String json = new String(ATOMXXTea.decrypt(data, this.xxTeaKey));
        System.out.println(json);
    }

    public void processControl(byte[] data) {
        System.out.println(Arrays.toString(data));
        int commandType = getCommandType(data);
        if (commandType == 1) {
            int responseCode = ((data[10] & 0xFF) << 8) + (data[9] & 0xFF);
            int length = getDataLength(data);
            byte[] responseBytes = Arrays.copyOfRange(data, 15, 15 + getDataLength(data));
            System.out.println("response code:" + responseCode + " data:" + Arrays.toString(responseBytes));
            if (responseCode == RequestCode.getRequestCode(1) + 1) {
                responseConnectRequest(responseBytes);
            }
            if (responseCode == RequestCode.getRequestCode(3) + 1) {
                parseResponse(responseBytes);
            }
            if (responseCode == RequestCode.getRequestCode(4) + 1) {
                parseResponse(responseBytes);
            }
        }
    }

    public void process(byte[] data) {
        this.dataPosition = 0;
        this.dataSize = data.length;
        this.tmpData = data;
        boolean z = false;
        do {
            int D = searchHeader(this.tmpData, this.dataPosition, this.dataSize);
            if (D != -1) {
                this.dataPosition = D;
                if (this.dataSize - D >= 15) {
                    byte[] header = new byte[15];
                    System.arraycopy(this.tmpData, D, header, 0, 15);
                    int dataLen = checkLocalData(header, true);
                    if (dataLen < 0) {
                        continue;
                    } else {
                        if (dataLen <= (this.dataSize - this.dataPosition) - 15) {
                            dataLen = dataLen + 15;
                            byte[] procData = new byte[dataLen];
                            this.processData = procData;
                            System.arraycopy(this.tmpData, this.dataPosition, procData, 0, dataLen);
                            System.out.println(D + " " + Arrays.toString(this.processData));
                            if (this.processData != null) {
                                processControl(this.processData);
                                this.dataPosition = this.dataPosition + dataLen;
                                z = true;
                                continue;
                            }
                        }
                    }
                }
            }
            z = false;
        } while (z);
    }

    private int getDataLength(byte[] data) {
        return ((data[12] & 0xFF) << 8) + (data[11] & 0xFF);
    }

    private int getCommandType(byte[] data) {
        if ((data[0] == headerLocal[0] && data[1] == headerLocal[1] && data[2] == headerLocal[2] && data[3] == headerLocal[3]) ||
                data[0] == headerLocal2[0] && data[1] == headerLocal2[1] && data[2] == headerLocal2[2] && data[3] == headerLocal2[3]) {
            return 2;
        }
        if (data[0] == header[0] && data[1] == header[1]) {
            return 1;
        }
        if (data[0] == headerLog[0] && data[1] == headerLog[1]) {
            return 3;
        }
        return -1;
    }

    public static int checkLocalData(byte[] bArr, boolean z) {
        if (bArr.length < 15) {
            return -1;
        }
        int byteArray2int = (bArr[12] << 8 & 0xFF00) + (bArr[11] & 0xFF);
        if (z) {
            return byteArray2int;
        }
        if (byteArray2int + 15 <= bArr.length) {
            return byteArray2int;
        }
        return -3;
    }

    private int searchHeader(byte[] data, int start, int end) {
        while (start < end - 2) {
            if (data[start] == 72 && data[start + 1] == 76) {
                return start;
            }
            start++;
        }
        return -1;
    }

    private int searchLocalHead(byte[] data, int i, int i2) {
        while (i < i2 - 4) {
            if (data[i] == headerLocal[0] && data[i + 1] == headerLocal[1] && data[i + 2] == headerLocal[2] && data[i + 3] == headerLocal[3]) {
                return i;
            }
            if (data[i] == headerLocal2[0] && data[i + 1] == headerLocal2[1] && data[i + 2] == headerLocal2[2] && data[i + 3] == headerLocal2[3]) {
                return i;
            }
            i++;
        }
        return -1;
    }

}
