package net.minecraft.resources.load;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

public class DatapackLoader {
    private final DatapackClassLoader loader;

    public DatapackLoader(File file){
        try {
            loader = new DatapackClassLoader(new ZipFile(file), System.class.getClassLoader());
        }catch (IOException ex){
            System.out.println("ERRRааррр");
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public InputStream getResource(String name){
        return loader.getResourceAsStream(name);
    }

    public Class<?> loadClass(String name){
        try {
            return loader.loadClass(name);
        }catch (ClassNotFoundException ex){
            throw new RuntimeException(ex);
        }
    }
}
