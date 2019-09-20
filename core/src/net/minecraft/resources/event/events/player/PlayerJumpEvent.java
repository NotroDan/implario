package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.Player;

public class PlayerJumpEvent extends APlayerСancelableEvent {
	public PlayerJumpEvent(Player player){
		super(player);
	}
}
