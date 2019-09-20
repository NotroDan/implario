package net.minecraft.block;

import net.minecraft.entity.player.Player;
import net.minecraft.resources.event.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class FenceClickedEvent implements Event {

	private final Player player;
	private final World world;
	private final BlockPos pos;
	public boolean returnValue;

	public FenceClickedEvent(Player player, World world, BlockPos pos) {
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

	public Player getPlayer() {
		return player;
	}


}
