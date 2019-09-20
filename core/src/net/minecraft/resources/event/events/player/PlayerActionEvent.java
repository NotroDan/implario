package net.minecraft.resources.event.events.player;

import lombok.Getter;
import net.minecraft.entity.player.Player;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@Getter
public class PlayerActionEvent extends APlayerEvent {
	public PlayerActionEvent(Player player, C0BPacketEntityAction.Action action, int aux){
		super(player);
		this.action = action;
		this.aux = aux;
	}

	private final C0BPacketEntityAction.Action action;
	private final int aux;
}