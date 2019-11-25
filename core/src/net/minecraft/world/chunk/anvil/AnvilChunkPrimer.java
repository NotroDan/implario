package net.minecraft.world.chunk.anvil;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.IChunkPrimer;

public class AnvilChunkPrimer implements IChunkPrimer {
	private final short[] data = new short[65536];
	private final IBlockState defaultState = Blocks.air.getDefaultState();

	@Override
	public IBlockState getBlockState(int x, int y, int z) {
		int i = x << 12 | z << 8 | y;
		return this.getBlockState(i);
	}

	@Override
	public void setBlockState(int x, int y, int z, IBlockState state) {
		int i = x << 12 | z << 8 | y;
		this.setBlockState(i, state);
	}

	private void setBlockState(int index, IBlockState state) {
		if (index < 0 || index >= this.data.length) throw new IndexOutOfBoundsException("The coordinate is out of range");
		if(state == null)state = Blocks.air.getDefaultState();
		this.data[index] = (short) Block.getStateId(state);
	}

	private IBlockState getBlockState(int index) {
		if (index < 0 || index >= this.data.length) throw new IndexOutOfBoundsException("The coordinate is out of range");

		IBlockState iblockstate = Block.getStateById(this.data[index]);
		return iblockstate != null ? iblockstate : this.defaultState;
	}
}
