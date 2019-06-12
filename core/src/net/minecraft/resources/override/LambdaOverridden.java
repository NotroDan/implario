package net.minecraft.resources.override;

public class LambdaOverridden<T> extends OverriddenEntry<T> {

	private final Overrider overrider;

	public LambdaOverridden(int id, String address, T old, T neo, Overrider<T> overrider) {
		super(id, address, old, neo);
		this.overrider = overrider;
	}

	@Override
	public void override(int id, String address, T element) {
		overrider.override(id, address, element);
	}

	@FunctionalInterface
	public interface Overrider<T> {

		void override(int id, String address, T element);

	}

}
