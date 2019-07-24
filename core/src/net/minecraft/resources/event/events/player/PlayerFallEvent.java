package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.EntityPlayer;

@Data
@EqualsAndHashCode (callSuper = false)
public class PlayerFallEvent extends PlayerEvent<PlayerFallEvent> {

	private final EntityPlayer player;
	private final float distance, damageMultiplier;

}
