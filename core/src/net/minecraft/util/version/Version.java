package net.minecraft.util.version;

import oogle.util.byteable.Decoder;
import oogle.util.byteable.Encoder;

public interface Version<T> {
    int version();

    T decode(final Decoder decoder);

    void encode(final Encoder encoder, final T object);
}
