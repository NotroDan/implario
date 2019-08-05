package net.minecraft.util.byteable;

import lombok.Setter;

import java.nio.charset.StandardCharsets;

public class SlowDecoder implements Decoder{
    private final byte array[];
    private int i, metadataI = 2;
    private byte metadata;
    private byte bitMetadata = 0;

    public SlowDecoder(byte array[]){
        this.array = array;
        i = (((array[0] & 0xFF) << 8) | (array[1] & 0xFF)) + 2;
        if(i != 2) metadata = array[metadataI++];
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

    @Setter
    private boolean useCompressOfShort = false;

    @Override
    public short readShort() {
        if(useCompressOfShort)
            if(readBoolean())
                return (short)(array[i++] & 0xFF);
        return (short)(((array[i++] & 0xFF) << 8) | (array[i++] & 0xFF));
    }

    @Setter
    private int sizeCompressOfInt = -1;

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
        return ((array[i++] & 0xFF) << 24) | ((array[i++] & 0xFF) << 16) | ((array[i++] & 0xFF) << 8) | (array[i++] & 0xFF);
    }

    @Setter
    public int sizeCompressOfLong;

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
        return (((long)array[i++] & 0xFF) << 56) | (((long)array[i++] & 0xFF) << 48) | (((long)array[i++] & 0xFF) << 40) |
                (((long)array[i++] & 0xFF) << 32) | (((long)array[i++] & 0xFF) << 24) | (((long)array[i++] & 0xFF) << 16) |
                (((long)array[i++] & 0xFF) << 8) | ((long)array[i++] & 0xFF);
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Setter
    private boolean usingCompressACSII;

    @Override
    public String readStr() {
        if(usingCompressACSII) {
            boolean bool = readBoolean();
            return new String(readBytes(), bool ? StandardCharsets.US_ASCII : StandardCharsets.UTF_8);
        }
        return new String(readBytes(), StandardCharsets.UTF_8);
    }
}
