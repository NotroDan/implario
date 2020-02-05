package net.minecraft.util;

import java.util.ArrayList;
import java.util.List;

public class ByteZip {
    private final List<byte[]> list = new ArrayList<>();

    public byte[] build(){
        byte result[] = new byte[size()];
        int write = 0;
        for(byte array[] : list){
            byte size[] = Coder.getSize(array.length);
            result = Coder.addBytes(size, write, result);
            write += size.length;
            result = Coder.addBytes(array, write, result);
            write += array.length;
        }
        return result;
    }

    public ByteZip add(Object object){
        return add(Coder.toBytes(object));
    }

    public ByteZip add(byte array[]){
        list.add(array);
        return this;
    }

    public int size(){
        int size = list.size();
        for(byte array[] : list)
            size += array.length + (array.length > 126 ? 4 : 0);
        return size;
    }
}
