package net.minecraft.util.crypt;

import lombok.Getter;
import oogle.util.byteable.*;

public class SecurityKey {
    @Getter
    private ECDSA rootKey;
    @Getter
    private TimedSertificate timed;

    public SecurityKey(ECDSA rootKey, TimedSertificate timed){
        this.rootKey = rootKey;
        this.timed = timed;
    }

    public boolean check(boolean rootUpdate, byte update[], byte signed[]){
        if(rootKey == null)return true;
        if(rootUpdate || timed == null)
            return rootKey.verify(update, signed);
        return timed.getSert().verify(update, signed);
    }

    public byte[] verifily(boolean rootUpdate, byte update[]){
        if(rootKey == null)return update;
        if(rootUpdate || timed == null)
            return rootKey.signature(update);
        return timed.getSert().signature(update);
    }

    public boolean sertificateEnded(){
        return timed.ended();
    }

    public void createSecrtificate(ECDSA sert, long end){
        if(rootKey == null)throw new IllegalStateException("Root key is null");
        timed = new TimedSertificate(end, sert, rootKey);
    }

    public static SecurityKey decodePrivate(Decoder decoder){
        return new SecurityKey(decoder.readBoolean() ? ECDSA.decodePrivate(decoder) : null,
                decoder.readBoolean() ? TimedSertificate.decodePrivate(decoder) : null);
    }

    public static SecurityKey decodePublic(Decoder decoder){
        return new SecurityKey(decoder.readBoolean() ? ECDSA.decodePublic(decoder) : null,
                decoder.readBoolean() ? TimedSertificate.decodePublic(decoder) : null);
    }

    public BytesEncoder encodePrivate(BytesEncoder encoder){
        encoder.writeBoolean(rootKey != null);
        if(rootKey != null)rootKey.encodePrivate(encoder);
        encoder.writeBoolean(timed != null);
        if(timed != null)timed.encodePrivate(encoder);
        return encoder;
    }

    public BytesEncoder encodePublic(BytesEncoder encoder){
        encoder.writeBoolean(rootKey != null);
        if(rootKey != null)rootKey.encodePublic(encoder);
        encoder.writeBoolean(timed != null);
        if(timed != null)timed.encodePublic(encoder);
        return encoder;
    }

    public static SecurityKey decodePrivate(byte array[]){
        return decodePrivate(SlowDecoder.defaultDecoder(array));
    }

    public static SecurityKey decodePublic(byte array[]){
        return decodePublic(SlowDecoder.defaultDecoder(array));
    }

    public byte[] encodePrivate(){
        return encodePrivate(SlowEncoder.defaultEncoder()).generate();
    }

    public byte[] encodePublic(){
        return encodePublic(SlowEncoder.defaultEncoder()).generate();
    }
}
