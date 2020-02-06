package net.minecraft.util.crypt;

import oogle.util.byteable.*;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class ECDSA {
    private static final String ALGORIZHM = "EC";
    private static final String SignareturAlgorizhm = "SHA1withECDSA";

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    //256, 384, 571, я не знаю почему 571, но оно работает
    public ECDSA(int keySize, SecureRandom random) {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance(ALGORIZHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        generator.initialize(keySize, random);
        KeyPair pair = generator.generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
    }

    public ECDSA(int keySize){
        this(keySize, new SecureRandom());
    }

    public ECDSA(PublicKey publicKey, PrivateKey privateKey){
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public byte[] signature(byte[] data) {
        try {
            Signature signature = Signature.getInstance(SignareturAlgorizhm);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public boolean verify(byte[] data, byte[] signData) {
        try {
            Signature signature = Signature.getInstance(SignareturAlgorizhm);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signData);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static BytesEncoder createEncoder(){
        SlowEncoder encoder = new SlowEncoder();
        encoder.setSizeCompressOfInt(2);
        return encoder;
    }

    public byte[] encodePublic(){
        BytesEncoder encoder = createEncoder();
        encodePublic(encoder);
        return encoder.generate();
    }

    public void encodePublic(Encoder encoder){
        encoder.writeBytes(publicKey.getEncoded());
    }

    public byte[] encodePrivate(){
        BytesEncoder encoder = createEncoder();
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

    public static ECDSA decodePublic(byte array[]){
        return decodePublic(createDecoder(array));
    }

    public static ECDSA decodePublic(Decoder decoder){
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance(ALGORIZHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            return new ECDSA(factory.generatePublic(new X509EncodedKeySpec(decoder.readBytes())), null);
        }catch (InvalidKeySpecException key){
            throw new RuntimeException(key);
        }
    }

    public static ECDSA decodePrivate(byte array[]){
        return decodePrivate(createDecoder(array));
    }

    public static ECDSA decodePrivate(Decoder decoder){
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance(ALGORIZHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            return new ECDSA(factory.generatePublic(new X509EncodedKeySpec(decoder.readBytes())),
                    factory.generatePrivate(new PKCS8EncodedKeySpec(decoder.readBytes())));
        }catch (InvalidKeySpecException key){
            throw new RuntimeException(key);
        }
    }
}
