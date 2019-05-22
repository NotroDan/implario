package net.minecraft.resources.event.events.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.resources.event.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockDropEvent extends Event {

	private final World world;
	private final BlockPos pos;
	private final IBlockState block;
	private final float chance;
	private final int fortune;
	private boolean isDefaultDropCancelled;

	public BlockDropEvent(World w, BlockPos pos, IBlockState block, float chance, int fortune) {
		this.world = w;
		this.pos = pos;
		this.block = block;
		this.chance = chance;
		this.fortune = fortune;
	}

	public boolean isDefaultDropCancelled() {
		return isDefaultDropCancelled;
	}
	public void cancelDefaultDrop() {
		isDefaultDropCancelled = true;
	}

	public BlockPos getPosition() {
		return pos;
	}

	public World getWorld() {
		return world;
	}

	public float getChance() {
		return chance;
	}

	public IBlockState getBlock() {
		return block;
	}

	public int getFortune() {
		return fortune;
	}


}
