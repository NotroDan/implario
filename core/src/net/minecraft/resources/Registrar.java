package net.minecraft.resources;

import net.minecraft.resources.event.Event;
import net.minecraft.resources.event.EventManager;
import net.minecraft.resources.event.Listener;

import java.util.Map;
import java.util.TreeSet;

public class Registrar {

	private final Domain domain;
	private final Map<<Class<? extends Event>, Listener<? extends Event>>> set = new TreeSet<>();

	public Registrar(Domain domain) {
		this.domain = domain;
	}

	public Domain getDomain() {
		return domain;
	}

	public void reg(Class<? extends Event> c, Listener<? extends Event> listener) {
		set.add(listener);
		EventManager.registerListener(c, listener);
	}

	public void unregister() {
		EventManager.LISTENERS.
	}

}
