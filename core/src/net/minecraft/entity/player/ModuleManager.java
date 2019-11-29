package net.minecraft.entity.player;

import net.minecraft.logging.Log;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Domain;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.byteable.FastDecoder;
import net.minecraft.util.byteable.FastEncoder;

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

    static byte[] removePlayerInfo(Datapack datapack) {
        if (MinecraftServer.mcServer != null) {
            ModuleManager manager = datapack.moduleManager();
            Encoder encoder = new FastEncoder();
            int players = 0;
            for (Player player : MinecraftServer.mcServer.getConfigurationManager().getPlayers()) {
                Module module = manager.getModule(player);
                if (module == null) continue;
                players++;
            }
            encoder.writeInt(players);
            for (Player player : MinecraftServer.mcServer.getConfigurationManager().getPlayers()) {
                Module module = manager.getModule(player);
                if (module == null) continue;
                try {
                    encoder.writeString(player.getName());
                    if(manager.supportedWorld())
                        encoder.writeBytes(manager.encodeWorld(module));
                    if(manager.supportedGlobal())
                        encoder.writeBytes(manager.encodeGlobal(module));
                    if(manager.supportedMemory())
                        encoder.writeBytes(manager.encodeMemory(module));
                } catch (Throwable throwable) {
                    Log.MAIN.error("Error on write nbt data, domain " + datapack.getDomain() + " module manager " + module.manager(), throwable);
                }
                manager.clearModule(player);
            }
            return encoder.generate();
        }
        return null;
    }

    static void loadPlayerInfo(Datapack datapack, byte array[]) {
        if (MinecraftServer.mcServer == null) return;
        Decoder decoder = new FastDecoder(array);
        int size = decoder.readInt();
        ModuleManager manager = datapack.moduleManager();
        if (manager == null) {
            Log.MAIN.warn("ModuleManager on datapack " + datapack.moduleManager() + " not found, but nbt data founded");
            return;
        }
        for (int i = 0; i < size; i++) {
            try {
                String player = decoder.readStr();
                Module module = manager.createEmptyModule();
                if(manager.supportedWorld())
                    manager.decodeWorld(module, decoder.readBytes());
                if(manager.supportedGlobal())
                    manager.decodeGlobal(module, decoder.readBytes());
                if(manager.supportedMemory())
                    manager.decodeMemory(module, decoder.readBytes());
                Player mplayer = MinecraftServer.mcServer.getConfigurationManager().getPlayerByUsername(player);
                if (mplayer == null) continue;
                manager.setModule(mplayer, module);
            } catch (Throwable throwable) {
                Log.MAIN.error("Error on read nbt data, domain " + datapack.getDomain() + " module manager " + datapack.moduleManager(), throwable);
            }
        }
    }
}
