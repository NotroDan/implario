package net.minecraft.util.version;

import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;

public interface Version<T> {
    int version();

    T decode(final Decoder decoder);

    void encode(final Encoder encoder, final T object);
}
