package com.fuwei.android.libcommon.utils;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {
    private static final String RSA = "RSA";
    //公钥
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnV7qA4Yq+I8B6pQ5lb5HIG5FrbuYtuztX8M64v6yjCZih2KB7doG7TuLFSWeCynkF997VxXOVvYYNDSJL7EDE1Hw+kHMnajjD1bolM1Mhu5GYl++qRPcfzlucami3h+qms/MJUo3A71in2fTDEfbomFCiNE/7HtgfyqBcR/2SfZlgk9Ph5k1i4rokKxzBNqwSgB6fofsvFntHvXcR0eHN3VsR+DrNgYYo2oK9ErWwCDDqec71T2WdM9o1PeO3nqgKXk/WOhjFnWNFOObJyHKMfsGJNerltkPqKEQxoYVElHDfIlbWzpJNfGwBzy4c0S15XqOUq9960VlY5esr0MUlwIDAQAB";
    //私钥
    private static final String PRIVATE_KEY = "填写你的私钥";

    /** 非对称加密 */
    public static String getEncodeData(String s) {
        try {
            byte[] buffer = Base64.decode(PUBLIC_KEY, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(s.getBytes());
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    /** 非对称解密 */
    public static String getDecodeData(String s) {
        try {
            byte[] decode = Base64.decode(PRIVATE_KEY, Base64.DEFAULT);
            PrivateKey privateKey =
                    KeyFactory.getInstance(RSA).generatePrivate(new
                            PKCS8EncodedKeySpec(decode));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] result = cipher.doFinal(Base64.decode(s,Base64.DEFAULT));
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
