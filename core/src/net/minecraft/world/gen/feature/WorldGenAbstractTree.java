package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public abstract class WorldGenAbstractTree extends WorldGenerator {

	public WorldGenAbstractTree(boolean p_i45448_1_) {
		super(p_i45448_1_);
	}

	protected boolean func_150523_a(Block b) {
		Material material = b.getMaterial();
		return material == Material.air || material == Material.leaves || b == Blocks.grass || b == Blocks.dirt || b == Blocks.log || b == Blocks.log2 || b == Blocks.sapling || b == Blocks.vine;
	}

	public void func_180711_a(World world, Random random, BlockPos pos) {
	}

	protected void func_175921_a(World worldIn, BlockPos p_175921_2_) {
		if (worldIn.getBlockState(p_175921_2_).getBlock() != Blocks.dirt) {
			this.setBlockAndNotifyAdequately(worldIn, p_175921_2_, Blocks.dirt.getDefaultState());
		}
	}

}
