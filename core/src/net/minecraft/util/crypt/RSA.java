package net.minecraft.util.crypt;

import __google_.util.Exceptions;
import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.byteable.SlowDecoder;
import net.minecraft.util.byteable.SlowEncoder;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {
    private static final String ALGORIZHM = "RSA";

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public RSA(int keySize, SecureRandom random){
        KeyPairGenerator generator = Exceptions.getThrowsEx(() -> KeyPairGenerator.getInstance(ALGORIZHM));
        generator.initialize(keySize, random);
        KeyPair pair = generator.generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
    }

    public RSA(int keySize){
        this(keySize, new SecureRandom());
    }

    public RSA(PublicKey publicKey, PrivateKey privateKey){
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public byte[] encode(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORIZHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            return cipher.doFinal(data);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public byte[] decode(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORIZHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Encoder createEncoder(){
        SlowEncoder encoder = new SlowEncoder();
        encoder.setSizeCompressOfInt(2);
        return encoder;
    }

    public byte[] encodePublic(){
        Encoder encoder = createEncoder();
        encodePublic(encoder);
        return encoder.generate();
    }

    public void encodePublic(Encoder encoder){
        encoder.writeBytes(publicKey.getEncoded());
    }

    public byte[] encodePrivate(){
        Encoder encoder = createEncoder();
        encodePrivate(encoder);
        return encoder.generate();
    }

    public void encodePrivate(Encoder encoder){
        encoder.writeBytes(publicKey.getEncoded()).writeBytes(privateKey.getEncoded());
    }

    private static Decoder createDecoder(byte array[]){
        SlowDecoder decoder = new SlowDecoder(array);
        decoder.setSizeCompressOfInt(2);
        return decoder;
    }

    public static RSA decodePublic(byte array[]){
        return decodePublic(createDecoder(array));
    }

    public static RSA decodePublic(Decoder decoder){
        KeyFactory factory = Exceptions.getThrowsEx(() -> KeyFactory.getInstance(ALGORIZHM));
        try {
            return new RSA(factory.generatePublic(new X509EncodedKeySpec(decoder.readBytes())), null);
        }catch (InvalidKeySpecException key){
            throw new RuntimeException(key);
        }
    }

    public static RSA decodePrivate(byte array[]){
        return decodePrivate(createDecoder(array));
    }

    public static RSA decodePrivate(Decoder decoder){
        KeyFactory factory = Exceptions.getThrowsEx(() -> KeyFactory.getInstance(ALGORIZHM));
        try {
            return new RSA(factory.generatePublic(new X509EncodedKeySpec(decoder.readBytes())),
                    factory.generatePrivate(new PKCS8EncodedKeySpec(decoder.readBytes())));
        }catch (InvalidKeySpecException key){
            throw new RuntimeException(key);
        }
    }
}
