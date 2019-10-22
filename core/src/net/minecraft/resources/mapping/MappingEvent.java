package net.minecraft.resources.mapping;

import net.minecraft.resources.event.Event;
import net.minecraft.resources.event.EventManager;
import net.minecraft.resources.event.Listener;

public class MappingEvent<T extends Event> implements Mapping{
    private final EventManager<T> manager;
    private final Listener<T> listener;

    public MappingEvent(EventManager<T> manager, Listener<T> listener){
        this.manager = manager;
        this.listener = listener;
    }

    @Override
    public void apply() {
        manager.add(listener);
    }

    @Override
    public void revert() {
        manager.remove(listener);
    }
}
