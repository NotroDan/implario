package net.minecraft.resources.event;

import net.minecraft.resources.Domain;

public abstract class Bridge<D extends Listenable> {

	public HandlerLibrary<D> LIB = new HandlerLibrary<>();

	public <T extends D> void registerListener(Domain domain, Class<T> c, Handler<D, T> l) {
		LIB.register(domain, c, l);
	}

	public void disable(Domain domain) {
		LIB.disable(domain);
	}
}
