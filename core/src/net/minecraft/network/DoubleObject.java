package net.minecraft.network;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
class DoubleObject<A, B> {
    static final Map<Class<? extends Packet>, ConnectionState> STATES_BY_CLASS = new HashMap<>();

    public final A one;
    public final B two;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleObject<?, ?> that = (DoubleObject<?, ?>) o;
        return Objects.equals(one, that.one);
    }

    @Override
    public int hashCode(){
        return one.hashCode();
    }
}
