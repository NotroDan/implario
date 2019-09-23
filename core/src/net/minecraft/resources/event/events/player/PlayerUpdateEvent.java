package net.minecraft.resources.event.events.player;

import net.minecraft.entity.player.Player;

public class PlayerUpdateEvent extends APlayerСancelableEvent {
	public PlayerUpdateEvent(Player player){
		super(player);
	}
}
