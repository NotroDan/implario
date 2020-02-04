package net.minecraft.security;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;

public class MinecraftSecurityManager extends SecurityManager{
    @Override
    public void checkLink(String lib) {}

    @Override
    public void checkExec(String cmd) {
        if(!Restart.restarting)throw new SecurityException("Run process disabled");
        Restart.restarting = false;
    }

    @Override
    public void checkWrite(String file) {
        try {
            if (!new File(file).getCanonicalPath().startsWith(new File(".").getCanonicalPath()))
                throw new SecurityException("Write files not in using directory disabled");
        }catch (IOException ex){}
    }

    //@Override
    //public void checkConnect(String host, int port) {
    //    System.out.println(host + " " + port);
    //}можно будет поюзать в сервере

    @Override
    public void checkPermission(Permission perm) {
        if (perm.getName().equalsIgnoreCase("setSecurityManager"))
            throw new SecurityException();
    }
}
