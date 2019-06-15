package net.minecraft.resources.load;

import net.minecraft.Logger;
import net.minecraft.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DatapackClassLoader extends ClassLoader{
    private final Map<String, ZipEntry> datapack;
    private final ZipFile file;

    public DatapackClassLoader(ZipFile file, ClassLoader parent){
        super(parent);
        this.datapack = new HashMap<>();
        this.file = file;
        Enumeration<? extends ZipEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            datapack.put(entry.getName(), entry);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            ZipEntry entry = datapack.get(name.replace('.', '/') + ".class");
            if (entry == null) throw new IOException();
            byte[] b = loadClassFromFile(entry);
            return defineClass((name.replace('/', '.')), b, 0, b.length);
        } catch (IOException ex) {
            return DatapackClassLoader.getSystemClassLoader().loadClass(name);
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        try {
            return file.getInputStream(datapack.get(name));
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public void close(){
        try {
            file.close();
        }catch (IOException ex){
            Logger.instance.error(ex);
        }
    }

    private byte[] loadClassFromFile(ZipEntry entry) throws IOException{
        InputStream inputStream = file.getInputStream(entry);
        byte[] buffer = new byte[(int)entry.getSize()];
        int i = inputStream.read(buffer);
        if(i != buffer.length)
        while (true) {
            if (i != buffer.length) {
                int b = inputStream.read(buffer, i, buffer.length - i);
                i = b + i;
            }else break;
        }
        inputStream.close();
        return buffer;
    }
}
