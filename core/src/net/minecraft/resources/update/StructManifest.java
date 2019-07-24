package net.minecraft.resources.update;

import __google_.util.ByteUnzip;
import __google_.util.ByteZip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructManifest {
    private final Map<String, byte[]> needUpdate = new HashMap<>();
    private final List<String> needRemove = new ArrayList<>();

    public void addNeedUpdate(String name, byte array[]){
        needUpdate.put(name, array);
    }

    public void addRemove(String name){
        needRemove.add(name);
    }

    public ManifestUpdate toManifestUpdate(){
        ByteZip zip = new ByteZip();
        zip.add(needUpdate.size());
        for(Map.Entry<String, byte[]> entry : needUpdate.entrySet())
            zip.add(entry.getKey()).add(entry.getValue());
        return ManifestUpdate.fromStructManifest(zip.build());
    }

    StructManifest(byte array[]){
        ByteUnzip unzip = new ByteUnzip(array);
        int end = unzip.getInt();
        for(int i = 0; i < end; i++)
            needUpdate.put(unzip.getString(), unzip.getBytes());
    }

    public StructManifest(){}

    public void writeTo(FileDatapackEdit edit){
        for(Map.Entry<String, byte[]> entry : needUpdate.entrySet())
            edit.add(entry.getKey(), entry.getValue());
        for(String str : needRemove)
            edit.remove(str);
    }
}
