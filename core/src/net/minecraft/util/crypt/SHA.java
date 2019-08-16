package net.minecraft.util.crypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {
    public static byte[] SHA_256(byte array[]){
        try {
            return MessageDigest.getInstance("SHA-256").digest(array);
        }catch (NoSuchAlgorithmException algm){
            //Unreal
            throw new Error("JVM not full");
        }
    }
}
