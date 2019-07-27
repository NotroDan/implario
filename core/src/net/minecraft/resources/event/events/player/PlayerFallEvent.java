package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.Player;

@Data
@EqualsAndHashCode (callSuper = false)
public class PlayerFallEvent extends PlayerEvent<PlayerFallEvent> {

	private final Player player;
	private final float distance, damageMultiplier;

}
