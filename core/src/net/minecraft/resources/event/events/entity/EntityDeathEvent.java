package net.minecraft.resources.event.events.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.resources.event.Event;
import net.minecraft.util.DamageSource;

@Data
@EqualsAndHashCode (callSuper = false)
public class EntityDeathEvent implements Event {
	private final EntityLivingBase entity;
	private final DamageSource cause;
}
