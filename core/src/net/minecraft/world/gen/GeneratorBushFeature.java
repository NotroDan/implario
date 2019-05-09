package net.minecraft.world.gen;

import net.minecraft.block.BlockBush;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class GeneratorBushFeature extends WorldGenerator {

	private BlockBush bush;

	public GeneratorBushFeature(BlockBush bush) {
		this.bush = bush;
	}

	public boolean generate(World worldIn, Random rand, BlockPos position) {
		for (int i = 0; i < 64; ++i) {
			BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

			if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.getHasNoSky() || blockpos.getY() < 255) && this.bush.canBlockStay(worldIn, blockpos,
					this.bush.getDefaultState())) {
				worldIn.setBlockState(blockpos, this.bush.getDefaultState(), 2);
			}
		}

		return true;
	}

}
