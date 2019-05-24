package vanilla.entity;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ParticleType;
import net.minecraft.world.World;
import vanilla.entity.monster.EntityIronGolem;
import vanilla.entity.monster.EntitySnowman;

public class VBlockPumpkin extends BlockPumpkin {

	@Override
	public void onBlockAdded(World w, BlockPos p, IBlockState s) {
		super.onBlockAdded(w, p, s);
		trySpawnGolem(w, p);
	}


	private static BlockPattern snowmanBasePattern;
	private static BlockPattern snowmanPattern;
	private static BlockPattern golemBasePattern;
	private static BlockPattern golemPattern;
	private static final Predicate<IBlockState> PUMPKIN_TEST = b -> b != null && (b.getBlock() == Blocks.pumpkin || b.getBlock() == Blocks.lit_pumpkin);

	private static void trySpawnGolem(World worldIn, BlockPos pos) {
		BlockPattern.PatternHelper blockpattern$patternhelper;

		if ((blockpattern$patternhelper = getSnowmanPattern().match(worldIn, pos)) != null) {
			for (int i = 0; i < getSnowmanPattern().getThumbLength(); ++i) {
				BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(0, i, 0);
				worldIn.setBlockState(blockworldstate.getPos(), Blocks.air.getDefaultState(), 2);
			}

			EntitySnowman entitysnowman = new EntitySnowman(worldIn);
			BlockPos blockpos1 = blockpattern$patternhelper.translateOffset(0, 2, 0).getPos();
			entitysnowman.setLocationAndAngles((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.05D, (double) blockpos1.getZ() + 0.5D, 0.0F, 0.0F);
			worldIn.spawnEntityInWorld(entitysnowman);

			for (int j = 0; j < 120; ++j) {
				worldIn.spawnParticle(ParticleType.SNOW_SHOVEL, (double) blockpos1.getX() + worldIn.rand.nextDouble(), (double) blockpos1.getY() + worldIn.rand.nextDouble() * 2.5D,
						(double) blockpos1.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
			}

			for (int i1 = 0; i1 < getSnowmanPattern().getThumbLength(); ++i1) {
				BlockWorldState blockworldstate1 = blockpattern$patternhelper.translateOffset(0, i1, 0);
				worldIn.notifyNeighborsRespectDebug(blockworldstate1.getPos(), Blocks.air);
			}
		} else if ((blockpattern$patternhelper = getGolemPattern().match(worldIn, pos)) != null) {
			for (int k = 0; k < getGolemPattern().getPalmLength(); ++k) {
				for (int l = 0; l < getGolemPattern().getThumbLength(); ++l) {
					worldIn.setBlockState(blockpattern$patternhelper.translateOffset(k, l, 0).getPos(), Blocks.air.getDefaultState(), 2);
				}
			}

			BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
			EntityIronGolem entityirongolem = new EntityIronGolem(worldIn);
			entityirongolem.setPlayerCreated(true);
			entityirongolem.setLocationAndAngles((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.05D, (double) blockpos.getZ() + 0.5D, 0.0F, 0.0F);
			worldIn.spawnEntityInWorld(entityirongolem);

			for (int j1 = 0; j1 < 120; ++j1) {
				worldIn.spawnParticle(ParticleType.SNOWBALL, (double) blockpos.getX() + worldIn.rand.nextDouble(), (double) blockpos.getY() + worldIn.rand.nextDouble() * 3.9D,
						(double) blockpos.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
			}

			for (int k1 = 0; k1 < getGolemPattern().getPalmLength(); ++k1) {
				for (int l1 = 0; l1 < getGolemPattern().getThumbLength(); ++l1) {
					BlockWorldState blockworldstate2 = blockpattern$patternhelper.translateOffset(k1, l1, 0);
					worldIn.notifyNeighborsRespectDebug(blockworldstate2.getPos(), Blocks.air);
				}
			}
		}
	}


	protected static BlockPattern getSnowmanBasePattern() {
		if (snowmanBasePattern == null) {
			snowmanBasePattern = FactoryBlockPattern.start().aisle(" ", "#", "#").where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow))).build();
		}

		return snowmanBasePattern;
	}

	protected static BlockPattern getSnowmanPattern() {
		if (snowmanPattern == null) {
			snowmanPattern = FactoryBlockPattern.start().aisle("^", "#",
					"#").where('^', BlockWorldState.hasState(PUMPKIN_TEST)).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow))).build();
		}

		return snowmanPattern;
	}

	protected static BlockPattern getGolemBasePattern() {
		if (golemBasePattern == null) {
			golemBasePattern = FactoryBlockPattern.start().aisle("~ ~", "###",
					"~#~").where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
		}

		return golemBasePattern;
	}

	protected static BlockPattern getGolemPattern() {
		if (golemPattern == null) {
			golemPattern = FactoryBlockPattern.start().aisle("~^~", "###",
					"~#~").where('^', BlockWorldState.hasState(PUMPKIN_TEST)).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block))).where('~',
					BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
		}

		return golemPattern;
	}


	public static boolean canDispenserPlace(World worldIn, BlockPos pos) {
		return getSnowmanBasePattern().match(worldIn, pos) != null || getGolemBasePattern().match(worldIn, pos) != null;
	}


}
