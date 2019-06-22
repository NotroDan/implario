package net.minecraft.resources;

import net.minecraft.server.Todo;

public abstract class Datapack implements ServerSideDatapack {

	private final Domain domain;
	protected final Registrar registrar;

	public Datapack(Domain domain) {
		this.domain = domain;
		this.registrar = new Registrar(domain);
	}

	public Domain getDomain() {
		return domain;
	}

	public static boolean isServerSide() {
		return Todo.instance.isServerSide();
	}

	public abstract void preinit();

	public abstract void init();

	public void ready() {

	}

	public void disable() {
		registrar.unregister();
	}

	protected abstract void unload();

	public abstract void loadBlocks();

	public abstract void loadItems();

	public Registrar getRegistrar() {
		return registrar;
	}

}
