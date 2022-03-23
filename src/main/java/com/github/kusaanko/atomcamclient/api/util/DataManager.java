package com.github.kusaanko.atomcamclient.api.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private static DataManager manager;
    private static final String dataPath = "data";

    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;
    private Cipher encryptor;
    private Cipher decryptor;

    private Map<String, byte[]> data;

    private DataManager() {
        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey = keyGen.generateKey();
            byte[] iv = {0x5e, 0x2b, (byte) 0xaa, 0x3a, (byte) 0x8b, 0x3d, 0x38, 0x79, 0x4a, 0x01, (byte) 0x83, 0x7f, (byte) 0xde, (byte) 0xab, 0x03, 0x57};
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            ivParameterSpec = new IvParameterSpec(iv);
            if (Files.exists(Paths.get("secret-key"))) {
                InputStream secretStream = Files.newInputStream(Paths.get("secret-key"));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buff = new byte[4096];
                int len;
                while ((len = secretStream.read(buff)) != -1) {
                    baos.write(buff, 0, len);
                }
                secretStream.close();
                secretKey = new SecretKeySpec(baos.toByteArray(), "AES");
                baos.close();
                baos = new ByteArrayOutputStream();
                InputStream aesStream = Files.newInputStream(Paths.get("aes-properties"));
                while ((len = aesStream.read(buff)) != -1) {
                    baos.write(buff, 0, len);
                }
                aesStream.close();
                AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
                parameters.init(baos.toByteArray());
                baos.close();
                encryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
                encryptor.init(Cipher.ENCRYPT_MODE, secretKey, parameters);
                decryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
                decryptor.init(Cipher.DECRYPT_MODE, secretKey, parameters);
            } else {
                encryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
                encryptor.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
                decryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
                decryptor.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            }
            OutputStream secretStream = Files.newOutputStream(Paths.get("secret-key"));
            secretStream.write(secretKey.getEncoded());
            secretStream.close();
            OutputStream aesStream = Files.newOutputStream(Paths.get("aes-properties"));
            aesStream.write(encryptor.getParameters().getEncoded());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        data = new HashMap<>();
    }

    public void save(OutputStream stream) {
        try {
            for (String key : this.data.keySet()) {
                byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                byte[] data = this.data.get(key);
                stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(keyBytes.length).array());
                stream.write(keyBytes);
                stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data.length).array());
                stream.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(InputStream stream) {
        this.data = new HashMap<>();
        try {
            byte[] len = new byte[4];
            while (stream.read(len) == 4) {
                byte[] keyBytes = new byte[ByteBuffer.wrap(len).order(ByteOrder.LITTLE_ENDIAN).getInt()];
                stream.read(keyBytes);
                byte[] dataLen = new byte[4];
                stream.read(dataLen);
                byte[] data = new byte[ByteBuffer.wrap(dataLen).order(ByteOrder.LITTLE_ENDIAN).getInt()];
                stream.read(data);
                this.data.put(new String(keyBytes, StandardCharsets.UTF_8), data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(byte[] data) {
        try {
            return this.encryptor.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public Object decrypt(byte[] data) {
        try {
            return this.decryptor.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public void writeString(String key, String value) {
        this.data.put(key, this.encrypt(value.getBytes(StandardCharsets.UTF_8)));
        try {
            this.save(Files.newOutputStream(Paths.get("data")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key, String defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return new String((byte[]) this.decrypt(data.get(key)), StandardCharsets.UTF_8);
    }

    public static DataManager getInstance() {
        if (manager == null) {
            manager = new DataManager();
        }
        return manager;
    }
}
