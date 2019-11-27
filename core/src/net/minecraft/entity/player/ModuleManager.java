package net.minecraft.entity.player;

import net.minecraft.resources.Domain;

public interface ModuleManager<T extends Module> {
    default byte[] encodeWorld(T module){
        return null;
    }

    default byte[] encodeGlobal(T module){
        return null;
    }

    Domain getDomain();

    T decode(byte world[], byte global[]);

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
