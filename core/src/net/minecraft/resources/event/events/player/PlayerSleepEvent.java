package net.minecraft.resources.event.events.player;

import lombok.*;
import net.minecraft.entity.player.Player;
import net.minecraft.util.BlockPos;

@Getter
public class PlayerSleepEvent extends APlayerСancelableEvent {
	private final BlockPos bedLocation;
	@Setter
	private Player.SleepStatus sleepStatus;

	public PlayerSleepEvent(Player player, BlockPos bedLocation, Player.SleepStatus sleepStatus){
		super(player);
		this.bedLocation = bedLocation;
		this.sleepStatus = sleepStatus;
	}
}
