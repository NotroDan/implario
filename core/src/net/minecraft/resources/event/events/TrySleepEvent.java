package net.minecraft.resources.event.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.event.Event;
import net.minecraft.util.BlockPos;

public class TrySleepEvent extends Event {

	private final EntityPlayer player;
	private final BlockPos bedLocation;
	private boolean cancelled;
	private EntityPlayer.EnumStatus status;

	public TrySleepEvent(EntityPlayer player, BlockPos bedLocation) {
		this.player = player;
		this.bedLocation = bedLocation;
	}

	public BlockPos getBedLocation() {
		return bedLocation;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public EntityPlayer.EnumStatus getStatus() {
		return status;
	}

	public void setStatus(EntityPlayer.EnumStatus status) {
		this.status = status;
	}

}
