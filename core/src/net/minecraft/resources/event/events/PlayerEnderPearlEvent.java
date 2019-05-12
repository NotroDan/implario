package net.minecraft.resources.event.events;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.resources.event.Event;

public class PlayerEnderPearlEvent extends Event {

	private final EntityEnderPearl pearl;
	private final EntityPlayerMP player;

	public PlayerEnderPearlEvent(EntityEnderPearl entityEnderPearl, EntityPlayerMP entitylivingbase) {
		this.pearl = entityEnderPearl;
		this.player = entitylivingbase;
	}


	public EntityPlayerMP getPlayer() {
		return player;
	}

	public EntityEnderPearl getPearl() {
		return pearl;
	}


}
