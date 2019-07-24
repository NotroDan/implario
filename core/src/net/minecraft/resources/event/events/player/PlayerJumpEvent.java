package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.EntityPlayer;

@Data
@EqualsAndHashCode (callSuper = false)
public class PlayerJumpEvent extends PlayerEvent<PlayerJumpEvent> {

	private final EntityPlayer player;

}
