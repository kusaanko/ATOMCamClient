package com.github.kusaanko.atomcamclient.api.av;

import com.github.kusaanko.atomcamclient.PcmAlaw;
import com.github.kusaanko.atomcamclient.api.av.audio.AudioData;
import com.github.kusaanko.atomcamclient.api.av.video.FrameInfo;
import com.github.kusaanko.atomcamclient.api.device.ATOMCamera;
import com.github.kusaanko.atomcamclient.api.p2p.command.CameraCommand;
import com.github.kusaanko.atomcamclient.api.p2p.command.CommandInfo;
import com.github.kusaanko.atomcamclient.api.p2p.command.CommandManager;
import com.github.kusaanko.atomcamclient.api.p2p.command.CommandProcessor;
import com.p2p.pppp_api.PPCS_API;
import com.p2p.pppp_api.PPCS_Error;

public class P2PAv {
    public static final int p2pType = 7;
    private static final byte controlChannel = 0;
    private static final byte videoChannel = 1;
    private static final byte audioChannel = 2;

    private String mac;
    private String p2pId;
    private String scloudAddress;
    private String enr;
    private byte[] xxTeaKey;
    private int sessionHandleId;
    private CommandProcessor commandProcessor;
    private ATOMCamera camera;

    public P2PAv(ATOMCamera camera) {
        this.commandProcessor = new CommandProcessor(this);
        this.camera = camera;
    }

    public boolean create(String mac, String p2pId, String scloudAddress, String enr) {
        this.setMac(mac);
        this.setP2pId(p2pId);
        this.setScloudAddress(scloudAddress);
        this.setEnr(enr);
        if (PPCS_API.INSTANCE.PPCS_Initialize(this.getScloudAddress().getBytes()) == PPCS_Error.ERROR_PPCS_SUCCESSFUL) {
            int session = PPCS_API.INSTANCE.PPCS_ConnectByServer(this.getP2PId(), (byte) 126, (char) 0, this.getScloudAddress());
            if (session > 0) {
                System.out.println("session " + session);
                this.setSessionHandleId(session);
                System.out.println("Connection success");
                return true;
            } else {
                System.out.println("Error" + session);
                return false;
            }
        } else {
            System.out.println("PPCS failed");
            return false;
        }
    }

    public void startConnectCamera() {
        this.sendCommand(CommandManager.createConnectRequest(this.getP2PId(), this.getMac()));
    }

    public void enableVideo() {
        // set video resolution to FullHD
        this.sendCommand(CommandManager.createSetVideoParams(120, 1, this.getXxTeaKey(), this.getP2PId(), this.getMac()));
        // start to send video
        this.sendCommand(CommandManager.createEnableMedia(CameraCommand.MediaType.VIDEO, true, this.getXxTeaKey(), this.getP2PId(), this.getMac()));
        // start to send audio
        this.sendCommand(CommandManager.createEnableMedia(CameraCommand.MediaType.AUDIO, true, this.getXxTeaKey(), this.getP2PId(), this.getMac()));
    }

    public void startPlayback(long timestamp) {
        this.sendCommand(CommandManager.createRequestPlayback(true, 1, timestamp / 1000, 0, this.getXxTeaKey(), this.getP2PId(), this.getMac()));
    }

    public void stopPlayback() {
        this.sendCommand(CommandManager.createRequestPlayback(false, 1, 0, 0, this.getXxTeaKey(), this.getP2PId(), this.getMac()));
    }

    public void sendCommand(CommandInfo info) {
        this.write(controlChannel, info.getData());
    }

    public void write(byte channel, byte[] data) {
        int ret = PPCS_API.INSTANCE.PPCS_Write(this.getSessionHandleId(), channel, data, data.length);
        System.out.println("write ret:" + ret);
    }

    public void readControl() {
        byte[] buff = new byte[5000];
        // Get frame header
        int ret = PPCS_API.INSTANCE.PPCS_Read(this.getSessionHandleId(), P2PAv.controlChannel, buff, new int[]{2000}, 100);
        if (ret == -3 && buff[0] != 0) {
            int len = buff[0];
            byte[] data = new byte[len];
            System.arraycopy(buff, 0, data, 0, len);
            this.commandProcessor.process(data);
        }
        if (ret >= 0) {
        } else {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    int count = 0;

    public byte[] getFrame() {
        byte[] buff = new byte[40];
        // Get frame header
        int ret = PPCS_API.INSTANCE.PPCS_Read(this.getSessionHandleId(), P2PAv.videoChannel, buff, new int[]{40}, 10000);
        if (ret >= 0) {
            FrameInfo frameInfo = new FrameInfo(buff, 40);
            if (frameInfo.getFrameLen() <= 0 || frameInfo.getFrameLen() > 200000) {
                return null;
            }
            byte[] frameBuffer = new byte[frameInfo.getFrameLen()];
            int ret2 = PPCS_API.INSTANCE.PPCS_Read(this.getSessionHandleId(), P2PAv.videoChannel, frameBuffer, new int[]{frameBuffer.length}, 10000);
            if (ret2 >= 0) {
                return frameBuffer;
            }
        } else {
            System.out.println(ret);
        }
        return null;
    }

    public AudioData getAudio() {
        byte[] buff = new byte[40];
        // Get frame information
        int ret = PPCS_API.INSTANCE.PPCS_Read(this.getSessionHandleId(), P2PAv.audioChannel, buff, new int[]{40}, 10000);
        if (ret >= 0) {
            FrameInfo frameInfo = new FrameInfo(buff, 40);
            if (frameInfo.getFrameLen() <= 0 || frameInfo.getFrameLen() > 200000) {
                return null;
            }
            byte[] frameBuffer = new byte[frameInfo.getFrameLen()];
            int ret2 = PPCS_API.INSTANCE.PPCS_Read(this.getSessionHandleId(), P2PAv.audioChannel, frameBuffer, new int[]{frameBuffer.length}, 10000);
            if (ret2 >= 0) {
                AudioData audioData = new AudioData(frameInfo);
                audioData.setData(frameBuffer);
                if (audioData.getCodecId() == AudioData.MEDIA_CODEC_AUDIO_G711_ALAW) {
                    byte[] pcm = PcmAlaw.decode(audioData.getData()).array();
                    audioData.setData(pcm);
                    audioData.setPcmSize(16);
                    audioData.setChannels(1);
                }
                return audioData;
            }
        } else {
            System.out.println(ret);
        }
        return null;
    }

    public void disconnect() {
        PPCS_API.INSTANCE.PPCS_Close(this.sessionHandleId);
        PPCS_API.INSTANCE.PPCS_ForceClose(this.sessionHandleId);
        PPCS_API.INSTANCE.PPCS_DeInitialize();
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getP2PId() {
        return p2pId;
    }

    public void setP2pId(String p2pId) {
        this.p2pId = p2pId;
    }

    public String getScloudAddress() {
        return scloudAddress;
    }

    public void setScloudAddress(String scloudAddress) {
        this.scloudAddress = scloudAddress;
    }

    public int getSessionHandleId() {
        return sessionHandleId;
    }

    public void setSessionHandleId(int sessionHandleId) {
        this.sessionHandleId = sessionHandleId;
    }

    public String getEnr() {
        return enr;
    }

    public void setEnr(String enr) {
        this.enr = enr;
    }

    public byte[] getXxTeaKey() {
        return xxTeaKey;
    }

    public void setXxTeaKey(byte[] xxTeaKey) {
        this.xxTeaKey = xxTeaKey;
        this.camera.setVerified(true);
    }
}
