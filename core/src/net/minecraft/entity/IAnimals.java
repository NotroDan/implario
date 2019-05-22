package net.minecraft.entity;

public interface IAnimals extends ITrackable {

	@Override
	default boolean sendVelocityUpdates() {
		return true;
	}

	@Override
	default int getUpdateFrequency() {
		return 3;
	}

	@Override
	default int getTrackingRange() {
		return 80;
	}

}
