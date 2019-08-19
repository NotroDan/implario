package net.minecraft.util.crypt;

import lombok.Getter;
import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.byteable.SlowDecoder;
import net.minecraft.util.byteable.SlowEncoder;

import java.util.Date;

public class TimedSertificate {
    @Getter
    private final long end;
    @Getter
    private final ECDSA sert;
    private final byte[] hash;

    public TimedSertificate(long end, ECDSA sert, ECDSA root){
        this.end = end;
        this.sert = sert;
        SlowEncoder encoder = new SlowEncoder();
        encoder.writeLong(end);
        sert.encodePublic(encoder);
        this.hash = root.signature(SHA.SHA_256(encoder.generate()));
    }

    private TimedSertificate(long end, ECDSA sert, byte hash[]){
        this.end = end;
        this.sert = sert;
        this.hash = hash;
    }

    public boolean verifily(ECDSA root){
        SlowEncoder encoder = new SlowEncoder();
        encoder.writeLong(end);
        sert.encodePublic(encoder);
        return root.verify(SHA.SHA_256(encoder.generate()), hash) && !ended();
    }

    public boolean ended(){
        return end < System.currentTimeMillis();
    }

    @Override
    public String toString(){
        return "End: " + new Date(end);
    }

    public static TimedSertificate decodePrivate(Decoder decoder){
        return new TimedSertificate(decoder.readLong(), ECDSA.decodePrivate(decoder), decoder.readBytes());
    }

    public static TimedSertificate decodePublic(Decoder decoder){
        return new TimedSertificate(decoder.readLong(), ECDSA.decodePublic(decoder), decoder.readBytes());
    }

    public Encoder encodePrivate(Encoder encoder){
        encoder.writeLong(end);
        sert.encodePrivate(encoder);
        encoder.writeBytes(hash);
        return encoder;
    }

    public Encoder encodePublic(Encoder encoder){
        encoder.writeLong(end);
        sert.encodePublic(encoder);
        encoder.writeBytes(hash);
        return encoder;
    }

    public static TimedSertificate decodePrivate(byte array[]){
        return decodePrivate(SlowDecoder.defaultDecoder(array));
    }

    public byte[] encodePrivate(){
        return encodePrivate(SlowEncoder.defaultEncoder()).generate();
    }

    public static TimedSertificate decodePublic(byte array[]){
        return decodePublic(SlowDecoder.defaultDecoder(array));
    }

    public byte[] encodePublic(){
        return encodePublic(SlowEncoder.defaultEncoder()).generate();
    }
}
