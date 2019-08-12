package net.minecraft.security;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.security.Permission;

public class MinecraftSecurityManager extends SecurityManager{

    @Override
    public void checkLink(String lib) {
    }

    @Override
    public void checkPermission(Permission perm) {
        if(perm instanceof FilePermission){
            if(perm.getActions().equals("write")){
                try {
                    if (!new File(perm.getName()).getCanonicalPath().startsWith(new File(".").getCanonicalPath()))
                        throw new SecurityException("Write files not in using directory disabled");
                }catch (IOException ex){}
            }
        }
    }
}
