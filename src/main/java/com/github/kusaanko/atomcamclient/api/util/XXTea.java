package com.github.kusaanko.atomcamclient.api.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class XXTea {
    private final int delta = (int) 2654435769L;

    private static int MX(int sum, int y, int z, int p, int e, int[] k) {
        return ((z >>> 5) ^ y << 2) + ((y >>> 3) ^ z << 4) ^ (sum ^ y) + (k[(int) (((long) p & 3) ^ e)] ^ z);
    }

    private XXTea() {
    }

    public static byte[] Encrypt(byte[] data, byte[] key) {
        return data.length == 0 ? data : XXTea.ToByteArray(XXTea.Encrypt(XXTea.ToUInt32Array(data, true), XXTea.ToUInt32Array(XXTea.FixKey(key), false)), false);
    }

    public static byte[] Encrypt(String data, byte[] key) {
        return Encrypt(data.getBytes(StandardCharsets.UTF_8), key);
    }

    public static byte[] Encrypt(byte[] data, String key) {
        return Encrypt(data, key.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] Encrypt(String data, String key) {
        return XXTea.Encrypt(data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
    }

    public static String EncryptToBase64String(byte[] data, byte[] key) {
        return Base64.getEncoder().encodeToString(XXTea.Encrypt(data, key));
    }

    public static String EncryptToBase64String(String data, byte[] key) {
        return Base64.getEncoder().encodeToString(XXTea.Encrypt(data, key));
    }

    public static String EncryptToBase64String(byte[] data, String key) {
        return Base64.getEncoder().encodeToString(XXTea.Encrypt(data, key));
    }

    public static String EncryptToBase64String(String data, String key) {
        return Base64.getEncoder().encodeToString(XXTea.Encrypt(data, key));
    }

    public static byte[] Decrypt(byte[] data, byte[] key) {
        return data.length == 0 ? data : XXTea.ToByteArray(XXTea.Decrypt(XXTea.ToUInt32Array(data, false), XXTea.ToUInt32Array(XXTea.FixKey(key), false)), true);
    }

    public static byte[] Decrypt(byte[] data, String key) {
        return XXTea.Decrypt(data, key.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] DecryptBase64String(String data, byte[] key) {
        return XXTea.Decrypt(Base64.getDecoder().decode(data), key);
    }

    public static byte[] DecryptBase64String(String data, String key) {
        return XXTea.Decrypt(Base64.getDecoder().decode(data), key);
    }

    public static String DecryptToString(byte[] data, byte[] key) {
        return new String(XXTea.Decrypt(data, key), StandardCharsets.UTF_8);
    }

    public static String DecryptToString(byte[] data, String key) {
        return new String(XXTea.Decrypt(data, key), StandardCharsets.UTF_8);
    }

    public static String DecryptBase64StringToString(String data, byte[] key) {
        return new String(XXTea.DecryptBase64String(data, key), StandardCharsets.UTF_8);
    }

    public static String DecryptBase64StringToString(String data, String key) {
        return new String(XXTea.DecryptBase64String(data, key), StandardCharsets.UTF_8);
    }

    private static int[] Encrypt(int[] v, int[] k)// unsigned
    {
        int index = v.length - 1;
        if (index < 1)
            return v;
        int z = v[index];// unsigned
        int sum = 0;// unsigned
        int num = 6 + 52 / (index + 1);
        while (0 < num--) {
            sum += (int) 2654435769L;
            int e = sum >> 2 & 3;// unsigned
            int p;
            long sumL = sum & 0xFFFFFFFFL;
            for (p = 0; p < index; p++) {
                int y = v[p + 1];
                z = (v[p] += XXTea.MX(sum, y, z, p, e, k));
            }
            int y1 = v[0];
            z = (v[index] += XXTea.MX(sum, y1, z, p, e, k));
        }
        return v;
    }

    private static int[] Decrypt(int[] v, int[] k)// unsigned
    {
        int index = v.length - 1;
        if (index < 1)
            return v;
        int y = v[0];// unsgined
        BigInteger bigInteger = BigInteger.valueOf(52 / (index + 1));
        bigInteger = bigInteger.multiply(BigInteger.valueOf(2654435769L));
        for (int sum = bigInteger.intValue() + 6; sum > 0; sum -= 2654435769L) {
            int e = sum >> 2 & 3;
            int p;
            for (p = index; p > 0; --p) {
                int z = v[p - 1];
                y = (v[p] -= XXTea.MX(sum, y, z, p, e, k));
            }
            int z1 = v[index];
            y = (v[0] -= XXTea.MX(sum, y, z1, p, e, k));
        }
        return v;
    }

    private static byte[] FixKey(byte[] key) {
        if (key.length == 16)
            return key;
        byte[] numArray = new byte[16];
        System.arraycopy(key, 0, numArray, 0, Math.min(key.length, 16));
        return numArray;
    }

    private static int[] ToUInt32Array(byte[] data, boolean includeLength) {
        int length1 = data.length;
        int length2 = (length1 & 0b11) == 0 ? length1 >> 2 : (length1 >> 2) + 1;
        int[] numArray;
        if (includeLength) {
            numArray = new int[length2 + 1];
            numArray[length2] = length1;
        } else
            numArray = new int[length2];
        for (int index = 0; index < length1; ++index)
            numArray[index >> 2] |= (int) (data[index] & 0xFF) << ((index & 3) << 3);
        return numArray;
    }

    private static byte[] ToByteArray(int[] data, boolean includeLength) {
        int length = data.length << 2;
        if (includeLength) {
            int num1 = data[data.length - 1];
            int num2 = length - 4;
            if (num1 < num2 - 3 || num1 > num2)
                return null;
            length = num1;
        }
        byte[] numArray = new byte[length];
        for (int index = 0; index < length; ++index)
            numArray[index] = (byte) (data[index >> 2] >> ((index & 3) << 3));
        return numArray;
    }
}
