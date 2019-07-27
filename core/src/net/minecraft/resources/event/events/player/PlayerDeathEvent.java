package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.Player;
import net.minecraft.util.DamageSource;

@Data
@EqualsAndHashCode (callSuper = false)
public class PlayerDeathEvent extends PlayerEvent<PlayerDeathEvent> {

	private final Player player;
	private final DamageSource cause;

}
