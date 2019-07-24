package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.EntityPlayer;

@EqualsAndHashCode(callSuper = false)
@Data
public class PlayerTickEvent extends PlayerEvent<PlayerTickEvent> {

	private final EntityPlayer player;

}
