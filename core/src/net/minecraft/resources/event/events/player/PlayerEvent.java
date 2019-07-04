package net.minecraft.resources.event.events.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.event.Event;

public abstract class PlayerEvent<T extends PlayerEvent<T>> extends Event<T> {

	public abstract EntityPlayer getPlayer();

}
