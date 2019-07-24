package net.minecraft.resources;

import net.minecraft.server.Todo;

public abstract class Datapack implements ServerSideDatapack {

	protected final Registrar registrar;
	private final Domain domain;

	public Datapack(Domain domain) {
		this.domain = domain;
		this.registrar = new Registrar(domain);
	}

	public static boolean isServerSide() {
		return Todo.instance.isServerSide();
	}

	public Domain getDomain() {
		return domain;
	}

	public abstract void preinit();

	public abstract void init();

	public void ready() {

	}

	public void disable() {
		registrar.unregister();
	}

	protected abstract void unload();

	public void loadBlocks() {}

	public void loadItems() {}

	public Registrar getRegistrar() {
		return registrar;
	}

	@Override
	public String toString() {
		return domain.getAddress();
	}

}
