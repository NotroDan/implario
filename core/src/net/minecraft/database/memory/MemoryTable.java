package net.minecraft.database.memory;

import __google_.util.FileIO;
import net.minecraft.database.Table;
import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.byteable.SlowDecoder;
import net.minecraft.util.byteable.SlowEncoder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class MemoryTable implements Table {
    private final Map<String, byte[]> map = new HashMap<>();
    private final boolean dumpEveryWrite;
    private final File file;

    MemoryTable(boolean dumpEveryWrite, File file){
        this.dumpEveryWrite = dumpEveryWrite;
        this.file = file;
        if(file != null && file.exists())readFromFile();
    }

    @Override
    public void write(String key, byte[] write) {
        map.put(key, write);
        if(dumpEveryWrite)writeToFile();
    }

    @Override
    public void delete(String key) {
        map.remove(key);
    }

    @Override
    public byte[] read(String key) {
        return map.get(key);
    }

    void delete(){
        map.clear();
        file.delete();
    }

    void close(){
        writeToFile();
    }

    private void writeToFile(){
        if(file == null)return;
        if(!file.exists())FileIO.create(file);
        Encoder encoder = SlowEncoder.defaultEncoder();
        encoder.writeInt(map.size());
        for(Map.Entry<String, byte[]> entry : map.entrySet())
            encoder.writeString(entry.getKey()).writeBytes(entry.getValue());
        FileIO.writeBytes(file, encoder.generate());
    }

    private void readFromFile(){
        if(file == null)return;
        Decoder decoder = SlowDecoder.defaultDecoder(FileIO.readBytes(file));
        int size = decoder.readInt();
        for(int i = 0; i < size; i++)
            map.put(decoder.readStr(), decoder.readBytes());
    }
}
