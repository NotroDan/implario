package net.minecraft.resources;

public abstract class Datapack {

	private final Domain domain;
	protected final Registrar registrar;

	public Datapack(Domain domain) {
		this.domain = domain;
		this.registrar = new Registrar(domain);
	}

	public Domain getDomain() {
		return domain;
	}

	public abstract void load();

	public void disable() {
		registrar.unregister();
	}

	protected abstract void unload();

}
