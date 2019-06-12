package net.minecraft.resources.override;

public abstract class OverriddenEntry<T> {

	public final int id;
	public final String address;
	public final T old;
	public final T neo;

	public OverriddenEntry(int id, String address, T old, T neo) {
		this.id = id;
		this.address = address;
		this.old = old;
		this.neo = neo;
	}

	public abstract void override(int id, String address, T element);

	public void override() {
		override(id, address, neo);
	}
	public void undo() {
		override(id, address, old);
	}

}
