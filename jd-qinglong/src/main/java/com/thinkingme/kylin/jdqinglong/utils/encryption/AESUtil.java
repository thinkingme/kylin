package com.thinkingme.kylin.jdqinglong.utils.encryption;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES加密，+BASE64（通过key，生成128位秘钥） key填充方式
 * 128位
 *
 * @author qcmoke
 */
public class AESUtil {

    /**
     * 密钥算法
     */
    public static final String ALGORITHM = "AES";

    /**
     * 加解密算法/工作模式/填充方式,Java6.0支持PKCS5Padding填充方式,BouncyCastle支持PKCS7Padding填充方式
     */
    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 生成密钥
     */
    public static String initKey() throws Exception {
        //实例化密钥生成器
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
        //初始化密钥生成器:AES要求密钥长度为128,192,256位
        kg.init(128);
        //生成密钥
        SecretKey secretKey = kg.generateKey();
        //获取二进制密钥编码形式
        return Base64.encodeBase64URLSafeString(secretKey.getEncoded());
    }


    /**
     * 转换密钥
     */
    public static Key toKey(byte[] key) throws Exception {
        return new SecretKeySpec(key, ALGORITHM);
    }


    /**
     * 加密数据
     *
     * @param data     待加密数据
     * @param password 密钥
     * @return 加密后的数据
     */
    public static String encrypt(String data, String password) throws Exception {
        Key key = getSecretKeySpec(password);
        //使用PKCS7Padding填充方式,这里就得这么写了(即调用BouncyCastle组件实现)
        //Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        //实例化Cipher对象，它用于完成实际的加密操作
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化Cipher对象，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //执行加密操作。加密后的结果通常都会用Base64编码进行传输
        return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes()));
    }

    private static SecretKeySpec getSecretKeySpec(String password) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes());
        kgen.init(128, secureRandom);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        return new SecretKeySpec(enCodeFormat, ALGORITHM);
    }


    /**
     * 解密数据
     *
     * @param data     待解密数据
     * @param password 密钥
     * @return 解密后的数据
     */
    public static String decrypt(String data, String password) throws Exception {
        Key key = getSecretKeySpec(password);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化Cipher对象，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        //执行解密操作
        return new String(cipher.doFinal(Base64.decodeBase64(data)));
    }


    public static void main(String[] args) throws Exception {
        System.out.println(Charset.defaultCharset().name());
        String source = "[{'clientId':'18','exchangeType':'1','happenGoldCount':'100','businessType':'204','context':'123123'}]";
        System.out.println("原文：" + source);

        //String key = initKey();
        String key = "12345678123";
        System.out.println("密钥：" + key + ",length=" + key.length());

        String encryptData = encrypt(source, key);
        byte[] bytes = encryptData.getBytes(StandardCharsets.UTF_8);
        System.out.println(bytes.length);
        System.out.println(encryptData);
        System.out.println("加密：" + encryptData);
        String decryptData = decrypt(encryptData, key);
        System.out.println("解密: " + decryptData);
    }
}
