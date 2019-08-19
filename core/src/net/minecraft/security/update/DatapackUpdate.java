package net.minecraft.security.update;

import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.util.byteable.SlowDecoder;
import net.minecraft.util.crypt.SecurityKey;

import javax.annotation.Signed;
import java.io.File;
import java.io.IOException;

public class DatapackUpdate {
    private final SecurityKey key;
    private String prefix, repo;
    private int version;

    public DatapackUpdate(DatapackLoader loader){
        byte array[] = loader.read("keys");
        if(array == null) key = null;
        else key = SecurityKey.decodePublic(array);
        array = loader.read("update");
        if(array == null){
            prefix = null;
            repo = null;
            version = -123;
        }else{
            String str = new String(array);
            String split[] = str.split("\n");
            if(split.length != 3)throw new IllegalArgumentException(":////");
            prefix = split[0];
            repo = split[1];
            version = Integer.parseInt(split[2]);
        }
    }

    public boolean checkUpdate(){
        //TODO: мэжик запрос к апи гитхаба
        throw new UnsupportedOperationException();
    }

    public void update(File datapack) throws IOException {
        JarFile jar = new JarFile(datapack);
        byte updates[][] = getUpdates();
        for(byte array[] : updates){
            SignedUpdate update = new SignedUpdate(SlowDecoder.defaultDecoder(array));
            update(jar, update);
        }
        jar.writeToJar(datapack);
    }

    public void update(JarFile file, SignedUpdate signed){
        Update update = signed.getUpdate();
        if(!signed.check(key))throw new RuntimeException("всо плоха");
        update.writeTo(file);
    }

    private byte[][] getUpdates(){
        //TODO: тут надо взять все апдейты
        throw new UnsupportedOperationException();
    }
}
