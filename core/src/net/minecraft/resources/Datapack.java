package net.minecraft.resources;

import net.minecraft.server.Todo;

import java.util.ArrayList;
import java.util.List;

public abstract class Datapack {

	public static final List<Datapack> LOADED = new ArrayList<>();

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

	public abstract void postinit();

	public void disable() {
		registrar.unregister();
	}

	protected abstract void unload();

	public abstract void loadBlocks();

	public abstract void loadItems();

}
