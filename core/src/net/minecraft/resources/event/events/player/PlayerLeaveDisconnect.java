package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.Player;
import net.minecraft.resources.event.Event;

public class PlayerLeaveDisconnect extends APlayerEvent {
	public PlayerLeaveDisconnect(Player player){
		super(player);
	}
}
