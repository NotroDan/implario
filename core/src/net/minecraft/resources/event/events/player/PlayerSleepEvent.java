package net.minecraft.resources.event.events.player;

import lombok.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

@ToString
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlayerSleepEvent extends PlayerEvent<PlayerSleepEvent> {

	private final EntityPlayer player;
	private final BlockPos bedLocation;
	private EntityPlayer.SleepStatus sleepStatus;

}
