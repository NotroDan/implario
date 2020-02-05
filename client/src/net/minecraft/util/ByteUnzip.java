package net.minecraft.util;

public class ByteUnzip {
    private final byte array[];
    private int i = 0;

    public ByteUnzip(byte array[]){
        this.array = array;
    }

    public String getString(){
        return Coder.toString(getBytes());
    }

    public boolean getBoolean(){
        return Coder.toBoolean(getBytes());
    }

    public long getLong(){
        return Coder.toLong(getBytes());
    }

    public int getInt(){
        return Coder.toInt(getBytes());
    }

    public short getShort(){
        return Coder.toShort(getBytes());
    }

    public byte getByte(){
        return Coder.toByte(getBytes());
    }

    public byte[] getBytes(){
        int size = Coder.getSize(array, i++);
        i = i + (size > 126 ? 4 : 0);
        byte result[] = Coder.subBytes(array, size, i);
        i = i + size;
        return result;
    }
}
