package net.minecraft.util.crypt;

import lombok.Getter;
import net.minecraft.security.update.SecurityKeys;
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

    public static TimedSertificate decode(byte array[]){
        SlowDecoder decoder = new SlowDecoder(array);
        decoder.setSizeCompressOfInt(2);
        decoder.setSizeCompressOfLong(6);
        return decode(decoder);
    }

    public static TimedSertificate decode(Decoder decoder){
        return new TimedSertificate(decoder.readLong(), ECDSA.decodePublic(decoder), decoder.readBytes());
    }

    public byte[] encode(){
        SlowEncoder encoder = new SlowEncoder();
        encoder.setSizeCompressOfInt(2);
        encoder.setSizeCompressOfLong(6);
        encode(encoder);
        return encoder.generate();
    }

    public void encode(Encoder encoder){
        encoder.writeLong(end);
        sert.encodePublic(encoder);
        encoder.writeBytes(hash);
    }

    public boolean verifily(){
        SlowEncoder encoder = new SlowEncoder();
        encoder.writeLong(end);
        sert.encodePublic(encoder);
        return SecurityKeys.rootKey.verify(SHA.SHA_256(encoder.generate()), hash) && (end > System.currentTimeMillis());
    }

    @Override
    public String toString(){
        return "End: " + new Date(end) + " verifily " + verifily();
    }
}
