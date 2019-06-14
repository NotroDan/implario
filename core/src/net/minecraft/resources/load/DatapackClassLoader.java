package net.minecraft.resources.load;

import javax.management.RuntimeErrorException;
import java.io.IOException;
import java.io.InputStream;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
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
            Class<?> _class = super.loadClass(name);
            return _class;
        } catch (ClassNotFoundException e1) {
            try {
                ZipEntry entry = datapack.get(name.replace('.', '/') + ".class");
                if(entry == null)throw new IOException();
                byte[] b = loadClassFromFile(entry);
                return defineClass((name.replace('/', '.')), b, 0, b.length);
            }catch (IOException ex){
                return DatapackClassLoader.getSystemClassLoader().loadClass(name);
            }
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

    private byte[] loadClassFromFile(ZipEntry entry) throws IOException{
        System.out.println(entry.getName());
        InputStream inputStream = file.getInputStream(entry);
        byte[] buffer = new byte[(int)entry.getSize()];
        inputStream.read(buffer);
        return buffer;
    }
}
