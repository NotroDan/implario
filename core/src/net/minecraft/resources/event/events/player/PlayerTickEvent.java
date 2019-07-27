package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.Player;

@EqualsAndHashCode (callSuper = false)
@Data
public class PlayerTickEvent extends PlayerEvent<PlayerTickEvent> {

	private final Player player;

}
