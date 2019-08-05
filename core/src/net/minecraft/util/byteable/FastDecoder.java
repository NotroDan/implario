package net.minecraft.util.byteable;

import java.nio.charset.StandardCharsets;

public class FastDecoder implements Decoder{
    private final byte array[];
    private int i = 0;

    public FastDecoder(byte array[]){
        this.array = array;
    }

    @Override
    public byte[] readBytes() {
        int read = readInt();
        byte array[] = new byte[read];
        for(int i = 0; i < read; i++)
            array[i] = this.array[this.i++];
        return array;
    }

    @Override
    public boolean readBoolean() {
        return array[i++] == 1;
    }

    @Override
    public byte readByte() {
        return (byte)(array[i++] & 0xFF);
    }

    @Override
    public short readShort() {
        return (short) (((array[i++] & 0xFF) << 8) | (array[i++] & 0xFF));
    }

    @Override
    public int readInt() {
        return ((array[i++] & 0xFF) << 24) | ((array[i++] & 0xFF) << 16) | ((array[i++] & 0xFF) << 8) | (array[i++] & 0xFF);
    }

    @Override
    public long readLong() {
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

    @Override
    public String readStr() {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }
}
