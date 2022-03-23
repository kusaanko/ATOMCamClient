package com.github.kusaanko.atomcamclient.api.util;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

// TODO: rename variables
public class HLXXTea {
    private static final int DELTA = -1640531527;

    public static IntBuffer decrypt(IntBuffer intBuffer, IntBuffer intBuffer2) {
        int[] iArr = new int[intBuffer.limit() - intBuffer.position()];
        intBuffer.get(iArr);
        return decryptInPlace(IntBuffer.wrap(iArr), intBuffer2);
    }

    public static IntBuffer decryptInPlace(IntBuffer intBuffer, IntBuffer intBuffer2) {
        if (intBuffer2.limit() != 4) {
            throw new IllegalArgumentException("XXTEA needs a 128-bits key");
        } else if (intBuffer.limit() < 2) {
            return intBuffer;
        } else {
            int i = intBuffer.get(0);
            int limit = ((52 / intBuffer.limit()) + 6) * DELTA;
            int limit2 = intBuffer.limit();
            do {
                int i2 = (limit >>> 2) & 3;
                int limit3 = intBuffer.limit() - 1;
                while (limit3 > 0) {
                    int i3 = intBuffer.get(limit3 - 1);
                    i = intBuffer.get(limit3) - (((i ^ limit) + (i3 ^ intBuffer2.get((limit3 & 3) ^ i2))) ^ (((i3 >>> 5) ^ (i << 2)) + ((i >>> 3) ^ (i3 << 4))));
                    intBuffer.put(limit3, i);
                    limit3--;
                }
                int i4 = intBuffer.get(limit2 - 1);
                i = intBuffer.get(0) - (((i ^ limit) + (intBuffer2.get(i2 ^ (limit3 & 3)) ^ i4)) ^ (((i4 >>> 5) ^ (i << 2)) + ((i >>> 3) ^ (i4 << 4))));
                intBuffer.put(0, i);
                limit += 1640531527;
            } while (limit != 0);
            return intBuffer;
        }
    }

    public static IntBuffer encrypt(IntBuffer intBuffer, IntBuffer intBuffer2) {
        int[] iArr = new int[intBuffer.limit() - intBuffer.position()];
        intBuffer.get(iArr);
        return encryptInPlace(IntBuffer.wrap(iArr), intBuffer2);
    }

    public static IntBuffer encryptInPlace(IntBuffer intBuffer, IntBuffer intBuffer2) {
        if (intBuffer2.limit() != 4) {
            throw new IllegalArgumentException("XXTEA needs a 128-bits key");
        } else if (intBuffer.limit() < 2) {
            return intBuffer;
        } else {
            int limit = intBuffer.limit();
            int limit2 = (52 / intBuffer.limit()) + 6;
            int i = limit - 1;
            int i2 = intBuffer.get(i);
            int i3 = 0;
            do {
                i3 -= 1640531527;
                int i4 = (i3 >>> 2) & 3;
                int i5 = 0;
                while (i5 < i) {
                    int i6 = i5 + 1;
                    int i7 = intBuffer.get(i6);
                    i2 = ((((i2 >>> 5) ^ (i7 << 2)) + ((i7 >>> 3) ^ (i2 << 4))) ^ ((i7 ^ i3) + (i2 ^ intBuffer2.get((i5 & 3) ^ i4)))) + intBuffer.get(i5);
                    intBuffer.put(i5, i2);
                    i5 = i6;
                }
                int i8 = intBuffer.get(0);
                i2 = ((((i2 >>> 5) ^ (i8 << 2)) + ((i8 >>> 3) ^ (i2 << 4))) ^ ((i8 ^ i3) + (i2 ^ intBuffer2.get(i4 ^ (i5 & 3))))) + intBuffer.get(i);
                intBuffer.put(i5, i2);
                limit2--;
            } while (limit2 > 0);
            return intBuffer;
        }
    }

    public boolean BytesEquals(byte[] bArr, byte[] bArr2, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (bArr[i2] != bArr2[i2]) {
                return false;
            }
        }
        return true;
    }

    public static int[] decrypt(int[] iArr, int[] iArr2) {
        return decrypt(IntBuffer.wrap(iArr), IntBuffer.wrap(iArr2)).array();
    }

    public static int[] encrypt(int[] iArr, int[] iArr2) {
        return encrypt(IntBuffer.wrap(iArr), IntBuffer.wrap(iArr2)).array();
    }

    public static ByteBuffer decrypt(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        byte[] bArr = new byte[byteBuffer.limit() - byteBuffer.position()];
        byteBuffer.get(bArr);
        return decryptInPlace(ByteBuffer.wrap(bArr), byteBuffer2);
    }

    public static ByteBuffer encrypt(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        byte[] bArr = new byte[byteBuffer.limit() - byteBuffer.position()];
        byteBuffer.get(bArr);
        return encryptInPlace(ByteBuffer.wrap(bArr), byteBuffer2);
    }

    public static byte[] decrypt(byte[] bArr, byte[] bArr2) {
        return decrypt(ByteBuffer.wrap(bArr), ByteBuffer.wrap(bArr2)).array();
    }

    public static byte[] encrypt(byte[] bArr, byte[] bArr2) {
        return encrypt(ByteBuffer.wrap(bArr), ByteBuffer.wrap(bArr2)).array();
    }

    public static int[] encryptInPlace(int[] iArr, int[] iArr2) {
        encryptInPlace(IntBuffer.wrap(iArr), IntBuffer.wrap(iArr2));
        return iArr;
    }

    public static int[] decryptInPlace(int[] iArr, int[] iArr2) {
        decryptInPlace(IntBuffer.wrap(iArr), IntBuffer.wrap(iArr2));
        return iArr;
    }

    public static byte[] encryptInPlace(byte[] bArr, byte[] bArr2) {
        encryptInPlace(ByteBuffer.wrap(bArr), ByteBuffer.wrap(bArr2));
        return bArr;
    }

    public static byte[] decryptInPlace(byte[] bArr, byte[] bArr2) {
        decryptInPlace(ByteBuffer.wrap(bArr), ByteBuffer.wrap(bArr2));
        return bArr;
    }

    public static ByteBuffer encryptInPlace(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        encryptInPlace(byteBuffer.asIntBuffer(), byteBuffer2.asIntBuffer());
        return byteBuffer;
    }

    public static ByteBuffer decryptInPlace(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        decryptInPlace(byteBuffer.asIntBuffer(), byteBuffer2.asIntBuffer());
        return byteBuffer;
    }

}
