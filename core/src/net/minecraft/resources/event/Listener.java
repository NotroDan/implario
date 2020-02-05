package net.minecraft.resources.event;

@FunctionalInterface
public interface Listener<T extends Event> {
	void process(T event);

	default boolean ignoreCancelled(){
		return false;
	}

	default int priority(){
	    return EventPriority.DEFAULT;
    }
}
