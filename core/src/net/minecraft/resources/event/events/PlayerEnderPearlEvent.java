package net.minecraft.resources.event.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.resources.event.events.player.PlayerEvent;

@Data
@EqualsAndHashCode (callSuper = false)
public class PlayerEnderPearlEvent extends PlayerEvent<PlayerEnderPearlEvent> {

	private final EntityEnderPearl pearl;
	private final EntityPlayerMP player;

}
