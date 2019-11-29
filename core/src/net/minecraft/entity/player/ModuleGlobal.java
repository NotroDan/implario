package net.minecraft.entity.player;

public interface ModuleGlobal<T extends Module> extends ModuleManager<T>{
    @Override
    default boolean supportedGlobal() {
        return true;
    }
}

