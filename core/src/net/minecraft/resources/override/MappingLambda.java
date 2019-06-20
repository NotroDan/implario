package net.minecraft.resources.override;

public class MappingLambda<T> extends Mapping<T> {

	private final Overrider overrider;

	public MappingLambda(int id, String address, T old, T neo, Overrider<T> overrider) {
		super(id, address, old, neo);
		this.overrider = overrider;
	}

	@Override
	public void map(int id, String address, T element) {
		overrider.override(id, address, element);
	}

	@FunctionalInterface
	public interface Overrider<T> {

		void override(int id, String address, T element);

	}

}
