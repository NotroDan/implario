package net.minecraft.resources.event.events.player;

import net.minecraft.entity.player.Player;

public class PlayerTickEvent extends APlayerEvent {
	public PlayerTickEvent(Player player){
		super(player);
	}
}
