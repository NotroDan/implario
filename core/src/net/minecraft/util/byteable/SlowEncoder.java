package net.minecraft.util.byteable;

import lombok.Setter;

import java.nio.charset.StandardCharsets;

public class SlowEncoder implements Encoder{
    private final ByteSet bytes = new ByteSet(), metadatas = new ByteSet();
    private byte metadata;
    private byte bitMetadata;

    @Override
    public Encoder writeBytes(byte[] array) {
        writeInt(array.length);
        for(byte b : array)
            bytes.write(b);
        return this;
    }

    @Override
    public Encoder writeBoolean(boolean b) {
        if(bitMetadata == 7){
            metadatas.write(metadata);
            metadata = 0;
            bitMetadata = 0;
        }
        if(b) metadata |= (byte)(1 << bitMetadata);
        bitMetadata++;
        return this;
    }

    @Override
    public Encoder writeByte(byte b) {
        bytes.write(b);
        return this;
    }

    @Setter
    private boolean useCompressOfShort = false;

    @Override
    public Encoder writeShort(short s) {
        if(useCompressOfShort){
            boolean bool = ((s & 0xFFFF) >>> 8) == s;
            writeBoolean(bool);
            if(bool){
                bytes.write((byte)s);
                return this;
            }
        }
        bytes.write((byte)(s << 8));
        bytes.write((byte)s);
        return this;
    }

    @Setter
    private int sizeCompressOfInt = -1;

    @Override
    public Encoder writeInt(int i) {
        if(sizeCompressOfInt != -1){
            boolean b = i >>> (sizeCompressOfInt << 3) == i;
            writeBoolean(b);
            if(b){
                for(int j = 0; j < sizeCompressOfInt; j++)
                    bytes.write((byte)(i >>> (j << 3)));
                return this;
            }
        }
        bytes.write((byte)(i >> 24));
        bytes.write((byte)(i >> 16));
        bytes.write((byte)(i >> 8));
        bytes.write((byte)i);
        return this;
    }

    @Setter
    private int sizeCompressOfLong = -1;

    @Override
    public Encoder writeLong(long l) {
        if(sizeCompressOfLong != -1){
            boolean b = l >>> (sizeCompressOfLong << 3) == l;
            writeBoolean(b);
            if(b){
                for(int j = 0; j < sizeCompressOfInt; j++)
                    bytes.write((byte)(l >>> (j << 3)));
                return this;
            }
        }
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

    @Setter
    private boolean usingCompressACSII;

    @Override
    public Encoder writeString(String s) {
        if(usingCompressACSII){
            boolean isACSII = true;
            for(char c : s.toCharArray())
                if(c > 0xFF){
                    isACSII = false;
                    break;
                }
            writeBoolean(usingCompressACSII);
            if(isACSII){
                writeBytes(s.getBytes(StandardCharsets.US_ASCII));
                return this;
            }
        }
        writeBytes(s.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    @Override
    public byte[] generate() {
        if(bitMetadata != 0)metadatas.write(metadata);
        byte array[] = new byte[metadatas.getSize() + bytes.getSize() + 2];
        array[0] = (byte)((metadatas.getSize() & 0xFF) >>> 8);
        array[1] = (byte)(metadatas.getSize());
        metadatas.generate(array, 2);
        bytes.generate(array, metadatas.getSize() + 2);
        return array;
    }
}
