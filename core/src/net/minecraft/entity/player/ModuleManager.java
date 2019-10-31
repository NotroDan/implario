package net.minecraft.entity.player;

import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;

public interface ModuleManager {
    void encode(Encoder encoder, Module module);

    Module decode(Decoder decoder);
}
