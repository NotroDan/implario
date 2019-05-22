package net.minecraft.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.event.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class FenceClickedEvent extends Event {

	private final EntityPlayer player;
	private final World world;
	private final BlockPos pos;
	public boolean returnValue;

	public FenceClickedEvent(EntityPlayer player, World world, BlockPos pos) {
		this.player = player;
		this.world = world;
		this.pos = pos;
	}

	public World getWorld() {
		return world;
	}

	public BlockPos getPos() {
		return pos;
	}

	public EntityPlayer getPlayer() {
		return player;
	}


}
