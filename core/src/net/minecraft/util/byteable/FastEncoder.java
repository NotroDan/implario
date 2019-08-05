package net.minecraft.util.byteable;

import java.nio.charset.StandardCharsets;

public class FastEncoder implements Encoder{
    private ByteSet bytes = new ByteSet();

    @Override
    public Encoder writeBytes(byte[] array) {
        writeInt(array.length);
        for(int i = 0; i < array.length; i++)
            bytes.write(array[i]);
        return this;
    }

    @Override
    public Encoder writeBoolean(boolean b) {
        writeByte((byte)(b ? 1 : 0));
        return this;
    }

    @Override
    public Encoder writeByte(byte b) {
        bytes.write(b);
        return this;
    }

    @Override
    public Encoder writeShort(short s) {
        bytes.write((byte)(s >> 8));
        bytes.write((byte)s);
        return this;
    }

    @Override
    public Encoder writeInt(int i) {
        bytes.write((byte)(i >> 24));
        bytes.write((byte)(i >> 16));
        bytes.write((byte)(i >> 8));
        bytes.write((byte)i);
        return this;
    }

    @Override
    public Encoder writeLong(long l) {
        bytes.write((byte)(l >> 56));
        bytes.write((byte)(l >> 48));
        bytes.write((byte)(l >> 40));
        bytes.write((byte)(l >> 32));
        bytes.write((byte)(l >> 24));
        bytes.write((byte)(l >> 16));
        bytes.write((byte)(l >> 8));
        bytes.write((byte)l);
        return this;
    }

    @Override
    public Encoder writeFloat(float f) {
        writeInt(Float.floatToIntBits(f));
        return this;
    }

    @Override
    public Encoder writeDouble(double d) {
        writeLong(Double.doubleToLongBits(d));
        return this;
    }

    @Override
    public Encoder writeString(String s) {
        writeBytes(s.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    @Override
    public byte[] generate() {
        return bytes.generate();
    }
}
