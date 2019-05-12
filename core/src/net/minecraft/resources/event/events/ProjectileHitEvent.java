package net.minecraft.resources.event.events;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.resources.event.Event;
import net.minecraft.util.MovingObjectPosition;

public class ProjectileHitEvent extends Event {

	private final EntityThrowable throwable;
	private final MovingObjectPosition object;

	public ProjectileHitEvent(EntityThrowable throwable, MovingObjectPosition object) {
		this.throwable = throwable;
		this.object = object;
	}

	public MovingObjectPosition getObject() {
		return object;
	}

	public EntityThrowable getThrowable() {
		return throwable;
	}

}
