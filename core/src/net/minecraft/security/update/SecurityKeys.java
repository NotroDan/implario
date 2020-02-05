package net.minecraft.security.update;

import net.minecraft.util.FileUtil;
import net.minecraft.util.crypt.AES;
import net.minecraft.util.crypt.ECDSA;
import net.minecraft.util.crypt.SecurityKey;
import oogle.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SecurityKeys {
    public static final SecurityKey root;

    public static ECDSA getTimed(String passwd, int rounds){
        try {
            URL url = SecurityKeys.class.getClassLoader().getResource("net/minecraft/security/update/privateTimedKey.aes");
            InputStream in = url.openStream();
            byte read[] = IOUtil.readBytes(new File("core/src/net/minecraft/security/update/privateTimedKey.aes"));
            FileUtil.readInputStream(in, read);
            AES aes = AES.generateAES(passwd, rounds);
            ECDSA ecdsa = ECDSA.decodePrivate(aes.decrypt(read));
            in.close();
            return ecdsa;
        }catch (Exception ex){
            throw new Error(ex);
        }
    }

    static{
        try{
            URL url = SecurityKeys.class.getClassLoader().getResource("net/minecraft/security/update/public.keys");
            InputStream in = url.openStream();
            byte read[] = new byte[in.available()];
            FileUtil.readInputStream(in, read);
            root = SecurityKey.decodePublic(read);
        }catch (IOException ex){
            throw new Error("Root key not found", ex);
        }
    }
}
