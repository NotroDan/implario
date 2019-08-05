package net.minecraft.util.byteable;

public interface Decoder {
    byte[] readBytes();

    boolean readBoolean();

    byte readByte();

    short readShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    String readStr();
}
