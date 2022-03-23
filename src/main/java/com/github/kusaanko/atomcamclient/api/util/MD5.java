package com.github.kusaanko.atomcamclient.api.util;

import java.security.MessageDigest;

public class MD5 {
    public static String encode(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            char[] charArray = str.toCharArray();
            byte[] bArr = new byte[charArray.length];
            for (int i = 0; i < charArray.length; i++) {
                bArr[i] = (byte) charArray[i];
            }
            byte[] digest = instance.digest(bArr);
            StringBuilder stringBuffer = new StringBuilder();
            for (byte b : digest) {
                int i = b & 255;
                if (i < 16) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(Integer.toHexString(i));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
