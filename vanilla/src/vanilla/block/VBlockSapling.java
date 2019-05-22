package vanilla.block;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import vanilla.world.gen.feature.*;

import java.util.Random;

public class VBlockSapling extends BlockSapling implements IGrowable {


	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.isClientSide) return;
		super.updateTick(worldIn, pos, state, rand);

		if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
			this.grow(worldIn, pos, state, rand);
		}
	}

	public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (state.getValue(STAGE) == 0) worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
		else this.generateTree(worldIn, pos, state, rand);
	}

	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return (double) worldIn.rand.nextFloat() < 0.45D;
	}

	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		this.grow(worldIn, pos, state, rand);
	}

	protected boolean func_181624_a(World w, BlockPos pos, int x, int y, BlockPlanks.EnumType z) {
		return this.isTypeAt(w, pos.add(x, 0, y), z) && this.isTypeAt(w, pos.add(x + 1, 0, y),
				z) && this.isTypeAt(w, pos.add(x, 0, y + 1), z) && this.isTypeAt(w,
				pos.add(x + 1, 0, y + 1), z);
	}


	public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		WorldGenerator worldgenerator = rand.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
		int i = 0;
		int j = 0;
		boolean flag = false;

		switch (state.getValue(TYPE)) {
			case SPRUCE:
				label114:
				for (i = 0; i >= -1; --i) {
					for (j = 0; j >= -1; --j) {
						if (this.func_181624_a(worldIn, pos, i, j, BlockPlanks.EnumType.SPRUCE)) {
							worldgenerator = new WorldGenMegaPineTree(false, rand.nextBoolean());
							flag = true;
							break label114;
						}
					}
				}

				if (!flag) {
					j = 0;
					i = 0;
					worldgenerator = new WorldGenTaiga2(true);
				}

				break;

			case BIRCH:
				worldgenerator = new WorldGenForest(true, false);
				break;

			case JUNGLE:
				IBlockState iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
				IBlockState iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.FALSE);
				label269:

				for (i = 0; i >= -1; --i) {
					for (j = 0; j >= -1; --j) {
						if (this.func_181624_a(worldIn, pos, i, j, BlockPlanks.EnumType.JUNGLE)) {
							worldgenerator = new WorldGenMegaJungle(true, 10, 20, iblockstate, iblockstate1);
							flag = true;
							break label269;
						}
					}
				}

				if (!flag) {
					j = 0;
					i = 0;
					worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
				}

				break;

			case ACACIA:
				worldgenerator = new WorldGenSavannaTree(true);
				break;

			case DARK_OAK:
				label390:
				for (i = 0; i >= -1; --i) {
					for (j = 0; j >= -1; --j) {
						if (this.func_181624_a(worldIn, pos, i, j, BlockPlanks.EnumType.DARK_OAK)) {
							worldgenerator = new WorldGenCanopyTree(true);
							flag = true;
							break label390;
						}
					}
				}

				if (!flag) {
					return;
				}

			case OAK:
		}

		IBlockState iblockstate2 = Blocks.air.getDefaultState();

		if (flag) {
			worldIn.setBlockState(pos.add(i, 0, j), iblockstate2, 4);
			worldIn.setBlockState(pos.add(i + 1, 0, j), iblockstate2, 4);
			worldIn.setBlockState(pos.add(i, 0, j + 1), iblockstate2, 4);
			worldIn.setBlockState(pos.add(i + 1, 0, j + 1), iblockstate2, 4);
		} else {
			worldIn.setBlockState(pos, iblockstate2, 4);
		}

		if (!worldgenerator.generate(worldIn, rand, pos.add(i, 0, j))) {
			if (flag) {
				worldIn.setBlockState(pos.add(i, 0, j), state, 4);
				worldIn.setBlockState(pos.add(i + 1, 0, j), state, 4);
				worldIn.setBlockState(pos.add(i, 0, j + 1), state, 4);
				worldIn.setBlockState(pos.add(i + 1, 0, j + 1), state, 4);
			} else {
				worldIn.setBlockState(pos, state, 4);
			}
		}
	}

}
