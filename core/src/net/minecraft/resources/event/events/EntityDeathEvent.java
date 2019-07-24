package net.minecraft.resources.event.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.resources.event.Event;
import net.minecraft.util.DamageSource;

@Data
@EqualsAndHashCode(callSuper = false)
public class EntityDeathEvent extends Event<EntityDeathEvent> {

	private final EntityLivingBase entity;
	private final DamageSource cause;

}
