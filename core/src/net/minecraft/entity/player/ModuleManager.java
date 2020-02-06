package net.minecraft.entity.player;

import net.minecraft.logging.Log;
import net.minecraft.resources.Datapack;
import net.minecraft.server.MinecraftServer;
import oogle.util.byteable.Decoder;
import oogle.util.byteable.Encoder;
import oogle.util.byteable.FastDecoder;
import oogle.util.byteable.FastEncoder;

public interface ModuleManager<T extends Module> {
    default byte[] encodeWorld(T module){
        return null;
    }

    default void decodeWorld(T module, byte array[]){}

    default boolean supportedWorld(){
        return false;
    }

    default byte[] encodeGlobal(T module){
        return null;
    }

    default void decodeGlobal(T module, byte array[]){}

    default boolean supportedGlobal(){
        return false;
    }

    default byte[] encodeMemory(T module){
        return null;
    }

    default void decodeMemory(T module, byte array[]){}

    default boolean supportedMemory(){
        return false;
    }

    T createEmptyModule();

    String getDomain();

    void writeID(int id);

    int readID();

    default T getModule(Player player){
        return player.getModule(readID());
    }

    default void setModule(Player player, T module){
        player.putModule(readID(), module);
    }

    default void clearModule(Player player){
        player.removeModule(readID());
    }
}
