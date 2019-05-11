package net.minecraft.resources.event.events;

import net.minecraft.entity.Entity;
import net.minecraft.resources.event.Event;

public class DamageByEntityEvent extends Event {

	private Entity damagedEntity, attacker;

	public DamageByEntityEvent(Entity damagedEntity, Entity attacker) {
		this.damagedEntity = damagedEntity;
		this.attacker = attacker;
	}

	public Entity getAttacker() {
		return attacker;
	}

	public Entity getDamagedEntity() {
		return damagedEntity;
	}

	public void setDamagedEntity(Entity damagedEntity) {
		this.damagedEntity = damagedEntity;
	}

	public void setAttacker(Entity attacker) {
		this.attacker = attacker;
	}

}
