package net.minecraft.util.byteable;

import lombok.Setter;

import java.nio.charset.StandardCharsets;

public class SlowDecoder implements Decoder{
    private final byte array[];
    private int i, metadataI;
    private byte metadata;
    private byte bitMetadata = 0;

    public SlowDecoder(byte array[], int offset){
        this.array = array;
        i = (((array[offset] & 0xFF) << 8) | (array[1 + offset] & 0xFF)) + 2 + offset;
        metadataI = 2 + offset;
        if(i != 2) metadata = array[metadataI++];
    }

    public SlowDecoder(byte array[]){
        this(array, 0);
    }

    @Override
    public byte[] readBytes() {
        byte array[] = new byte[readInt()];
        for(int i = 0; i < array.length; i++)
            array[i] = readByte();
        return array;
    }

    @Override
    public boolean readBoolean() {
        if(bitMetadata == 7){
            metadata = array[metadataI++];
            bitMetadata = 0;
        }
        return ((metadata >>> bitMetadata++) & 1) == 1;
    }

    @Override
    public byte readByte() {
        return (byte)(array[i++] & 0xFF);
    }

    private boolean useCompressOfShort = false;

    public SlowDecoder setUseCompressOfShort(boolean b) {
        useCompressOfShort = b;
        return this;
    }

    @Override
    public short readShort() {
        if(useCompressOfShort)
            if(readBoolean())
                return (short)(array[i++] & 0xFF);
        return (short)(((array[i++] & 0xFF) << 8) | (array[i++] & 0xFF));
    }

    private int sizeCompressOfInt = -1;

    public SlowDecoder setSizeCompressOfInt(int i) {
        sizeCompressOfInt = i;
        return this;
    }

    @Override
    public int readInt() {
        if(sizeCompressOfInt != -1){
            if(readBoolean()) {
                int rslt = 0;
                for (int i = 0; i < sizeCompressOfInt; i++)
                    rslt |= (array[this.i++] & 0xFF) << (i << 3);
                return rslt;
            }
        }
        return readIntDirectly();
    }

    public int sizeCompressOfLong;

    public SlowDecoder setSizeCompressOfLong(int i){
        sizeCompressOfLong = i;
        return this;
    }

    @Override
    public long readLong() {
        if(sizeCompressOfLong != -1){
            if(readBoolean()) {
                long rslt = 0;
                for (int i = 0; i < sizeCompressOfLong; i++)
                    rslt |= ((long)array[this.i++] & 0xFFL) << ((long)(i << 3));
                return rslt;
            }
        }
        return readLongDirectly();
    }

    private boolean readFloatDirectly = true;

    public SlowDecoder setReadFloatDirectly(boolean b) {
        readFloatDirectly = b;
        return this;
    }

    @Override
    public float readFloat() {
        if(readFloatDirectly)return Float.intBitsToFloat(readIntDirectly());
        return Float.intBitsToFloat(readInt());
    }

    private boolean readDoubleDirectly = true;

    public SlowDecoder setReadDoubleDirectly(boolean b) {
        readDoubleDirectly = b;
        return this;
    }

    @Override
    public double readDouble() {
        if(readDoubleDirectly)return Double.longBitsToDouble(readLongDirectly());
        return Double.longBitsToDouble(readLong());
    }

    private boolean usingCompressACSII;

    public SlowDecoder setUsingCompressACSII(boolean b){
        usingCompressACSII = b;
        return this;
    }

    @Override
    public String readStr() {
        if(usingCompressACSII) {
            boolean bool = readBoolean();
            return new String(readBytes(), bool ? StandardCharsets.US_ASCII : StandardCharsets.UTF_8);
        }
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    private int readIntDirectly(){
        return ((array[i++] & 0xFF) << 24) | ((array[i++] & 0xFF) << 16) | ((array[i++] & 0xFF) << 8) | (array[i++] & 0xFF);
    }

    private long readLongDirectly(){
        return (((long)array[i++] & 0xFF) << 56) | (((long)array[i++] & 0xFF) << 48) | (((long)array[i++] & 0xFF) << 40) |
                (((long)array[i++] & 0xFF) << 32) | (((long)array[i++] & 0xFF) << 24) | (((long)array[i++] & 0xFF) << 16) |
                (((long)array[i++] & 0xFF) << 8) | ((long)array[i++] & 0xFF);
    }

    public static SlowDecoder defaultDecoder(byte array[]){
        return new SlowDecoder(array).setSizeCompressOfInt(2).setSizeCompressOfLong(6).setUsingCompressACSII(true).setUseCompressOfShort(true);
    }
}
