package net.minecraft.resources.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
	public static final List<contol> list = new ArrayList<>();

	public static <T extends Event> T callEvent(T event) {
		for(contol contol : list){
			if(contol.clazz == event.getClass())
				contol.invoke(event);
		}
		return event;
	}

	public static void registerListener(Class<? extends Event> c, Listener l) {
		LISTENERS.computeIfAbsent(c, d -> new ArrayList<>()).add(l);
	}

	public static void unregisterListener(Class<? extends Event> c, Listener l) {
		List<Listener<? extends Event>> listeners = LISTENERS.get(c);
		if (listeners != null) listeners.remove(l);
	}

	private static class contol<T extends Event>{
		private final Class<T> clazz;
		private final List<Listener<T>> listeners = new ArrayList<>();

		public contol(Class<T> clazz){
			this.clazz = clazz;
		}

		private void invoke(T event){
			for(Listener<T> listener : listeners)
				listener.handle(event);
		}

		private void add(Listener<T> listener){
			listeners.add(listener);
		}
	}
}
