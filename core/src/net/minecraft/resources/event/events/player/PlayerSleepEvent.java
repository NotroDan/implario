package net.minecraft.resources.event.events.player;

import lombok.*;
import net.minecraft.entity.player.Player;
import net.minecraft.util.BlockPos;

@ToString
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode (callSuper = false)
public class PlayerSleepEvent extends PlayerEvent<PlayerSleepEvent> {

	private final Player player;
	private final BlockPos bedLocation;
	private Player.SleepStatus sleepStatus;

}
