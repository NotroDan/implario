package net.minecraft.resources.event.events.player;

import net.minecraft.entity.player.Player;
import net.minecraft.resources.event.Event;

public interface IPlayerEvent extends Event {
	Player getPlayer();
}
