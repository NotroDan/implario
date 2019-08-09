package net.minecraft.util.byteable;

public interface Encoder {
    Encoder writeBytes(byte array[]);

    Encoder writeBoolean(boolean b);

    Encoder writeByte(byte b);

    Encoder writeShort(short s);

    Encoder writeInt(int i);

    Encoder writeLong(long l);

    Encoder writeFloat(float f);

    Encoder writeDouble(double d);

    Encoder writeString(String s);

    byte[] generate();

    byte[] generate(byte array[], int offset);
}
