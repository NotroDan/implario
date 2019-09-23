package net.minecraft.resources;

import net.minecraft.server.Todo;

public abstract class Datapack implements ServerSideDatapack {

	private final Domain domain;
	protected final Registrar registrar;
	public Object clientSide;

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

	public void preinit(){}

	public void init(){}

	public void ready() {}

	public void disable() {
		registrar.unregister();
	}

	protected void unload(){}

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
