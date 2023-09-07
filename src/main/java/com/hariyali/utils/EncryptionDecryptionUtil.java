package com.hariyali.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Encryption and decryption utility class
 *
 * @Author Vinod
 * @Version 1.0
 * @Date 27/08/2023
 */
@Component
public class EncryptionDecryptionUtil {

    @Value("${encryption.key}")
    String encryptionKey;

    public String encrypt(String plainText) {

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecretKeySpec keyspec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(encryptionKey.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(Base64.getEncoder().encode(original));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }//method

    public String decrypt(String encryptedText) {

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encrypted1 = decoder.decode(new String(decoder.decode(encryptedText)));

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecretKeySpec keyspec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(encryptionKey.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted1);
            return new String(original).trim();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }//method

}//class
