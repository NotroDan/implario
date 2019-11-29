package net.minecraft.resources.event;

public abstract class Bridge<D extends Listenable> {

	public HandlerLibrary<D> LIB = new HandlerLibrary<>();

	public <T extends D> void registerListener(String domain, Class<T> c, Handler<D, T> l) {
		LIB.register(domain, c, l);
	}

	public void disable(String domain) {
		LIB.disable(domain);
	}

}
