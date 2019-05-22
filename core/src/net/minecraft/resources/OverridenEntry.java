package net.minecraft.resources;

class OverridenEntry<T> {

	final int id;
	final String address;
	final T old;
	final T neo;

	OverridenEntry(int id, String address, T old, T neo) {
		this.id = id;
		this.address = address;
		this.old = old;
		this.neo = neo;
	}

}
