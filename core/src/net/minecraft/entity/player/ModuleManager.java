package net.minecraft.entity.player;

import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;

public interface ModuleManager {
    byte[] encode(Module module);

    Module decode(byte array[]);
}
