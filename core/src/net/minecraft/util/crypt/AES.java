package net.minecraft.util.crypt;

import __google_.util.Exceptions;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static byte[] getRandomAESKey() {
        KeyGenerator keyGenerator = Exceptions.getThrowsEx(() -> KeyGenerator.getInstance(ALGORITHM));
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();
        return key.getEncoded();
    }

    private final SecretKey key;

    public AES(byte key[]){
        this.key = new SecretKeySpec(key, ALGORITHM);
    }

    public byte[] key(){
        return key.getEncoded();
    }

    public byte[] encrypt(byte data[]){
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public byte[] decrypt(byte data[]){
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
