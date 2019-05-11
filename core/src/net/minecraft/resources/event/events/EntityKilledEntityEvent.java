package net.minecraft.resources.event.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.resources.event.Event;

public class EntityKilledEntityEvent extends Event {

	private final Entity killer;
	private final EntityLivingBase killed;

	public EntityKilledEntityEvent(Entity killer, EntityLivingBase killed) {
		this.killer = killer;
		this.killed = killed;
	}

	public Entity getKiller() {
		return killer;
	}

	public EntityLivingBase getKilled() {
		return killed;
	}

}
