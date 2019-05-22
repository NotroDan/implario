package vanilla.block;

import net.minecraft.block.BlockMushroom;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import vanilla.world.gen.feature.WorldGenBigMushroom;
import vanilla.world.gen.feature.WorldGenerator;

import java.util.Random;

public class VBlockMushroom extends BlockMushroom implements IGrowable {

	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return (double) rand.nextFloat() < 0.4D;
	}

	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		this.generateBigMushroom(worldIn, pos, state, rand);
	}

	public boolean generateBigMushroom(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		worldIn.setBlockToAir(pos);
		WorldGenerator gen = new WorldGenBigMushroom(
				this == Blocks.brown_mushroom ? Blocks.brown_mushroom_block :
				this == Blocks.red_mushroom ? Blocks.red_mushroom_block :
						null);

		if (gen != null && gen.generate(worldIn, rand, pos)) return true;
		worldIn.setBlockState(pos, state, 3);
		return false;
	}


	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (rand.nextInt(25) != 0) return;
		int i = 5;
		int j = 4;

		for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4)))
			if (worldIn.getBlockState(blockpos).getBlock() == this) if (--i <= 0) return;

		BlockPos blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

		for (int k = 0; k < 4; ++k) {
			if (worldIn.isAirBlock(blockpos1) && this.canBlockStay(worldIn, blockpos1, this.getDefaultState()))
				pos = blockpos1;

			blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
		}

		if (worldIn.isAirBlock(blockpos1) && this.canBlockStay(worldIn, blockpos1, this.getDefaultState()))
			worldIn.setBlockState(blockpos1, this.getDefaultState(), 2);
	}

}
