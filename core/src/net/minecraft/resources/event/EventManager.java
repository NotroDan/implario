package net.minecraft.resources.event;

import net.minecraft.util.TokenMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

	public static final Map<Class<? extends Event>, List<Listener<? extends Event>>> LISTENERS = new HashMap<>();

	public static <T extends Event> T callEvent(T event) {
		List<Listener<? extends Event>> listeners = LISTENERS.get(event.getClass());
		if (listeners == null) return event;
		for (Listener<? extends Event> listener : listeners)
			listener.handle(event);
		return event;
	}

	public static void registerListener(Class<? extends Event> c, Listener l) {
		LISTENERS.computeIfAbsent(c, d -> new ArrayList<>()).add(l);
	}

	public static void unregisterListener(Class<? extends Event> c, Listener l) {
		List<Listener<? extends Event>> listeners = LISTENERS.get(c);
		if (listeners != null) listeners.remove(l);
	}

}
