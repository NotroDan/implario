package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.Player;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@Data
@EqualsAndHashCode (callSuper = false)
public class PlayerActionEvent extends PlayerEvent<PlayerActionEvent> {

	private final Player player;
	private final C0BPacketEntityAction.Action action;
	private final int aux;

}
