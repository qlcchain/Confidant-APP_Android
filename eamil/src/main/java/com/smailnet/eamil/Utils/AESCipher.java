package com.smailnet.eamil.Utils;


import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AESCipher {

    private static final String charset = "UTF-8";

    /**
     * 加密
     * @param content
     * @param key
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    public static String aesEncryptString(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] contentBytes = content.getBytes(charset);
        byte[] keyBytes = key.getBytes(charset);
        byte[] encryptedBytes = AESToolsCipher.aesEncryptBytes(contentBytes, keyBytes);
        return base64Encode2String(encryptedBytes);
    }

    /**
     * 解密
     * @param content
     * @param key
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    public static String aesDecryptString(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] encryptedBytes = base64Decode(content);
        byte[] keyBytes = key.getBytes(charset);
        byte[] decryptedBytes = AESToolsCipher.aesDecryptBytes(encryptedBytes, keyBytes);
        return new String(decryptedBytes, charset);
    }
    public static  byte[] aesDecryptByte(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] encryptedBytes = base64Decode(content);
        byte[] keyBytes = key.getBytes(charset);
        byte[] decryptedBytes = AESToolsCipher.aesDecryptBytes(encryptedBytes, keyBytes);
        return decryptedBytes;
    }
    public static String aesEncryptBytesToBase64(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return base64Encode2String(AESToolsCipher.cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE));
    }

    /**
     * aes解密
     * @param contentBytes
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    public static byte[] aesDecryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return AESToolsCipher.cipherOperation(contentBytes, keyBytes, Cipher.DECRYPT_MODE);
    }
    /**
     * Base64编码
     *
     * @param input 要编码的字节数组
     * @return Base64编码后的字符串
     */
    public static String base64Encode2String(byte[] input) {
        return Base64.encodeToString(input, Base64.NO_WRAP);
    }
    /**
     * Base64解码
     *
     * @param input 要解码的字符串
     * @return Base64解码后的字符串
     */
    public static byte[] base64Decode(String input) {
        if(input == null)
        {
            byte[] data = Base64.decode("", Base64.NO_WRAP);
            return data;
        }
        input =  input.replace("\\n", "");
        try {
            byte[] data = Base64.decode(input, Base64.NO_WRAP);
            return data;
        }catch (Exception e)
        {
            byte[] data = Base64.decode("", Base64.NO_WRAP);
            return data;
        }
    }
}
