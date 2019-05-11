package net.minecraft.resources.event.events;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.resources.event.Event;

public class PlayerEntityActionEvent extends Event {

	private final EntityPlayerMP player;
	private final C0BPacketEntityAction.Action action;
	private final int aux;

	public PlayerEntityActionEvent(EntityPlayerMP playerEntity, C0BPacketEntityAction.Action action, int auxData) {
		this.player = playerEntity;
		this.action = action;
		this.aux = auxData;
	}

	public EntityPlayerMP getPlayer() {
		return player;
	}

	public C0BPacketEntityAction.Action getAction() {
		return action;
	}

	public int getAux() {
		return aux;
	}

}
