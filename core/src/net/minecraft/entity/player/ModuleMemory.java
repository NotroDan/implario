package net.minecraft.entity.player;

public interface ModuleMemory<T extends Module> extends ModuleManager<T>{
    @Override
    default boolean supportedMemory() {
        return true;
    }
}
