package net.minecraft.entity.player;

public interface ModuleWorld<T extends Module> extends ModuleManager<T>{
    @Override
    default boolean supportedWorld() {
        return true;
    }
}

