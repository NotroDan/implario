package net.minecraft.security.update;

import net.minecraft.resources.load.DatapackLoader;

public class DatapackUpdate {


    public DatapackUpdate(DatapackLoader loader){
        byte array[] = loader.read("keys");
        if(array != null){

        }
    }
}
