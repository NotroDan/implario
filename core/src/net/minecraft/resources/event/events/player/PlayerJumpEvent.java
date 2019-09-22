package net.minecraft.resources.event.events.player;

import net.minecraft.entity.player.Player;

public class PlayerJumpEvent extends APlayerСancelableEvent {
	public PlayerJumpEvent(Player player){
		super(player);
	}
}
