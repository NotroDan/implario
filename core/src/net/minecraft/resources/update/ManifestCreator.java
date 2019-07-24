package net.minecraft.resources.update;

import __google_.util.ByteUnzip;
import __google_.util.ByteZip;
import __google_.util.FileIO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ManifestCreator {
    private final Map<String, byte[]> hash = new HashMap<>();
    private final Map<String, byte[]> files = new HashMap<>();

    public ManifestCreator(File cache, File inputJar) throws IOException {
        if(!cache.exists())cache.createNewFile();
        else if(cache.length() != 0){
            ByteUnzip unzip = new ByteUnzip(FileIO.readBytes(cache));
            int size = unzip.getInt();
            for(int i = 0; i < size; i++)
                hash.put(unzip.getString(), unzip.getBytes());
        }
        read(inputJar);
    }

    public StructManifest createManifest(File cache){
        StructManifest manifest = new StructManifest();
        Map<String, byte[]> nextHash = new HashMap<>();
        for(Map.Entry<String, byte[]> entry : files.entrySet()){
            byte hash[] = this.hash.get(entry.getKey());
            byte currentHash[] = RSA.hashing(entry.getValue());
            nextHash.put(entry.getKey(), currentHash);
            if(hash == null || !Arrays.equals(hash, currentHash))
                manifest.addNeedUpdate(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<String, byte[]> entry : files.entrySet())
            if(files.get(entry.getKey()) == null)
                manifest.addRemove(entry.getKey());
        save(cache, nextHash);
        return manifest;
    }

    private void save(File cache, Map<String, byte[]> hash){
        ByteZip zip = new ByteZip();
        zip.add(hash.size());
        hash.forEach((str, c) -> zip.add(str).add(c));
        FileIO.writeBytes(cache, zip.build());
    }

    private void read(File rec) throws IOException{
        FileDatapackEdit edit = new FileDatapackEdit(rec);
        this.files.putAll(edit.files);
    }
}
