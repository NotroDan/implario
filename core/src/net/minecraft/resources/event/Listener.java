package net.minecraft.resources.event;

import net.minecraft.resources.Domain;

public interface Listener<T extends Event> {
	void process(T event);

	Domain domain();

	default boolean ignoreCancelled(){
		return false;
	}

	default int priority(){
	    return 0;
    }
}
