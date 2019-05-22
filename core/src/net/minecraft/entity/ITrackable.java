package net.minecraft.entity;

public interface ITrackable {

	int getUpdateFrequency();

	int getTrackingRange();

	boolean sendVelocityUpdates();
}
