package net.minecraft.resources.mapping;

public abstract class LegacyMapping<T> extends Mapping<T> {

	protected final int id;

	public LegacyMapping(int id, String address, T overridden, T actual) {
		super(address, overridden, actual);
		this.id = id;
	}

}
