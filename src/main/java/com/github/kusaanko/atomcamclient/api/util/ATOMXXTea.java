package com.github.kusaanko.atomcamclient.api.util;

public class ATOMXXTea {
    public static byte[] decrypt(byte[] data, byte[] key) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(decrypt(toIntArray(data, false), toIntArray(key, false)), false);
    }

    public static byte[] encrypt(byte[] data, byte[] key) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(encrypt(toIntArray(data, false), toIntArray(key, false)), false);
    }

    private static byte[] toByteArray(int[] ints, boolean isIncludeLength) {
        int length = ints.length << 2;
        if (isIncludeLength) {
            int i = ints[ints.length - 1];
            if (i > length) {
                return null;
            }
            length = i;
        }
        byte[] bArr = new byte[length];
        for (int i2 = 0; i2 < length; i2++) {
            bArr[i2] = (byte) ((ints[i2 >>> 2] >>> ((i2 & 3) << 3)) & 255);
        }
        return bArr;
    }

    private static int[] toIntArray(byte[] bytes, boolean isIncludeLength) {
        int i;
        int[] ints;
        if ((bytes.length & 3) == 0) {
            i = bytes.length >>> 2;
        } else {
            i = (bytes.length >>> 2) + 1;
        }
        if (isIncludeLength) {
            ints = new int[i + 1];
            ints[i] = bytes.length;
        } else {
            ints = new int[i];
        }
        int length = bytes.length;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i2 >>> 2;
            ints[i3] = ints[i3] | ((bytes[i2] & 255) << ((i2 & 3) << 3));
        }
        return ints;
    }

    public static int[] decrypt(int[] data, int[] key) {
        int length = data.length;
        int i = length - 1;
        int i2 = data[i];
        int i3 = data[0];
        int i4 = (52 / length) * -1640531527 + 0xb54cda56;// + 0xb54cda56 is added from HLApi version
        int i5 = data[0];
        do {
            int i6 = (i4 >>> 2) & 3;
            int i7 = i;
            while (i7 > 0) {
                int i8 = data[i7 - 1];
                i5 = data[i7] - (((i5 ^ i4) + (i8 ^ key[(i7 & 3) ^ i6])) ^ (((i8 >>> 5) ^ (i5 << 2)) + ((i5 >>> 3) ^ (i8 << 4))));
                data[i7] = i5;
                i7--;
            }
            int i9 = data[i];
            i5 = data[0] - (((i5 ^ i4) + (key[i6 ^ (i7 & 3)] ^ i9)) ^ (((i9 >>> 5) ^ (i5 << 2)) + ((i5 >>> 3) ^ (i9 << 4))));
            data[0] = i5;
            i4 += 1640531527;
        } while (i4 != 0);
        return data;
    }

    public static int[] encrypt(int[] data, int[] key) {
        int length = data.length;
        int i = (52 / length) + 5; // changed from 6 to 5 from HLApi version
        int i2 = length - 1;
        int i3 = data[i2];
        int i4 = 0;
        do {
            //i4 -= 1640531527;
            i4 += 2654435769L;// this changed from HLApi version
            int i5 = (i4 >>> 2) & 3;
            int i6 = 0;
            while (i6 < i2) {
                int i7 = i6 + 1;
                int i8 = data[i7];
                i3 = ((((i3 >>> 5) ^ (i8 << 2)) + ((i8 >>> 3) ^ (i3 << 4))) ^ ((i8 ^ i4) + (i3 ^ key[(i6 & 3) ^ i5]))) + data[i6];
                data[i6] = i3;
                i6 = i7;
            }
            int i9 = data[0];
            i3 = ((((i3 >>> 5) ^ (i9 << 2)) + ((i9 >>> 3) ^ (i3 << 4))) ^ ((i9 ^ i4) + (i3 ^ key[i5 ^ (i6 & 3)]))) + data[i2];
            data[i2] = i3;
            i--;
        } while (i > 0);
        return data;
    }
}
