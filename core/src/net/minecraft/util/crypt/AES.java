package net.minecraft.util.crypt;

import __google_.util.Exceptions;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class AES {
    private static final String ALGORITHM = "AES";

    public static AES generateAES(String password, int rounds){
        return new AES(SHA.SHA_256(BCrypt.hashpw(password,
                                 BCrypt.gensalt(rounds,
                                         random(SHA.SHA_256(password.getBytes(StandardCharsets.UTF_8)))))
                .getBytes(StandardCharsets.UTF_8)));
    }

    private static SecureRandom random(byte seed[]){
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(seed);
            return sr;
        }catch (Exception ex){
            throw new Error();
        }
    }

    public static byte[] getRandomAESKey() {
        KeyGenerator keyGenerator = Exceptions.getThrowsEx(() -> KeyGenerator.getInstance(ALGORITHM));
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();
        return key.getEncoded();
    }

    public static byte[] getRandomAESKey(SecureRandom random){
        KeyGenerator keyGenerator = Exceptions.getThrowsEx(() -> KeyGenerator.getInstance(ALGORITHM));
        keyGenerator.init(256, random);
        SecretKey key = keyGenerator.generateKey();
        return key.getEncoded();
    }

    private final SecretKey key;

    public AES(byte key[]){
        this.key = new SecretKeySpec(key, ALGORITHM);
    }

    public static AES fromBase64(String base64){
        return new AES(Base64.decodeBase64(base64));
    }

    public byte[] key(){
        return key.getEncoded();
    }

    public byte[] encrypt(byte data[]){
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public byte[] decrypt(byte data[]){
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public String toBase64(){
        return Base64.encodeBase64String(key.getEncoded());
    }
}
