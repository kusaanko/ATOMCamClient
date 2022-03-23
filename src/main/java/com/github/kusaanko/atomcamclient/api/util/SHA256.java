package com.github.kusaanko.atomcamclient.api.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; bytes != null && i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 255);
            if (hexString.length() == 1) {
                sb.append('0');
            }
            sb.append(hexString);
        }
        return sb.toString().toLowerCase();
    }

    public static String encode(String str, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
            return toHex(mac.doFinal(str.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }
    }
}
