package net.minecraft.resources.event.events;

public interface Cancelable {
    void cancel(boolean cancel);

    boolean isCanceled();
}
