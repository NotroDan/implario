package net.minecraft.resources.event.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.resources.event.Event;
import net.minecraft.util.MovingObjectPosition;

@Data
@EqualsAndHashCode (callSuper = false)
public class ProjectileHitEvent extends Event<ProjectileHitEvent> {

	private final EntityThrowable throwable;
	private final MovingObjectPosition bumpedInto;

}
