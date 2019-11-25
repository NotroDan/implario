package net.minecraft.world.chunk;

import net.minecraft.block.state.IBlockState;

public interface IChunkPrimer {
    void setBlockState(int x, int y, int z, IBlockState state);

    IBlockState getBlockState(int x, int y, int z);
}
