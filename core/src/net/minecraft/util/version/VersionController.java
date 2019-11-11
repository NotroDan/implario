package net.minecraft.util.version;

import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.byteable.SlowDecoder;
import net.minecraft.util.byteable.SlowEncoder;

public class VersionController<T> {
    private final Version<T> before[], actual;

    @SafeVarargs
    public VersionController(final Version<T> actual, final Version<T>... before){
        this.actual = actual;
        this.before = before;
    }

    public void encodeSingle(final Encoder encoder, final T object) {
        encoder.writeInt(actual.version());
        actual.encode(encoder, object);
    }

    public T decodeSingle(final Decoder decoder){
        final Version<T> version = getVersion(decoder.readInt());
        return version == null ? null : version.decode(decoder);
    }

    public void encodeIterable(final Encoder encoder, final Iterable<T> array, int size){
        encoder.writeInt(actual.version()).writeInt(size);
        for(final T object : array)
            if(!saveEncode(encoder, object))return;
    }

    public void encodeArray(final Encoder encoder, final T[] array){
        encoder.writeInt(actual.version()).writeInt(array.length);
        for(final T object : array)
            if(!saveEncode(encoder, object))return;
    }

    @SuppressWarnings("unchecked")
    public T[] decodeArray(final Decoder decoder){
        final Version<T> version = getVersion(decoder.readInt());
        if(version == null)return null;
        final int size = decoder.readInt();
        T[] array = (T[])new Object[size];
        try{
            for(int i = 0; i < size; i++)
                array[i] = version.decode(SlowDecoder.defaultDecoder(decoder.readBytes()));
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        return array;
    }

    private Version<T> getVersion(final int versionID){
        if(versionID == actual.version()) return actual;
        for(final Version<T> version : before)
            if(version.version() == versionID)return version;
        return null;
    }

    private boolean saveEncode(final Encoder encoder, final T object){
        final Encoder enc = SlowEncoder.defaultEncoder();
        try{
            actual.encode(enc, object);
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        encoder.writeBytes(enc.generate());
        return true;
    }
}
