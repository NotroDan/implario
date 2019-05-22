package vanilla.entity;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import vanilla.entity.boss.EntityWither;

public class VItemSkull extends ItemSkull {

	private static final Predicate<BlockWorldState> IS_WITHER_SKELETON = s -> s.getBlockState() != null && s.getBlockState().getBlock() == Blocks.skull && s.getTileEntity() instanceof TileEntitySkull && ((TileEntitySkull) s.getTileEntity()).getSkullType() == 1;
	private static BlockPattern witherBasePattern;
	private static BlockPattern witherPattern;

	public static boolean canDispenserPlace(World worldIn, BlockPos pos, ItemStack stack) {
		return stack.getMetadata() == 1 &&
				pos.getY() >= 2 &&
				worldIn.getDifficulty() != EnumDifficulty.PEACEFUL &&
				!worldIn.isClientSide &&
				getWitherBasePattern().match(worldIn, pos) != null;
	}

	protected static BlockPattern getWitherBasePattern() {
		if (witherBasePattern != null) return witherBasePattern;

		witherBasePattern = FactoryBlockPattern
				.start().aisle("   ", "###", "~#~")
				.where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand)))
				.where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();

		return witherBasePattern;
	}

	protected static BlockPattern getWitherPattern() {
		if (witherPattern != null) return witherPattern;

		witherPattern = FactoryBlockPattern
				.start().aisle("^^^", "###", "~#~")
				.where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand)))
				.where('^', IS_WITHER_SKELETON)
				.where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
				.build();

		return witherPattern;
	}

	public static void checkWitherSpawn(World worldIn, BlockPos pos, TileEntitySkull te) {
		// Нельзя спавнить визера из головы невизера, при позиции ниже 2, на мирной сложности, даи вообще, не на клиенте это всё должно быть
		if (te.getSkullType() != 1 || pos.getY() < 2 || worldIn.getDifficulty() == EnumDifficulty.PEACEFUL || worldIn.isClientSide) return;
		BlockPattern pattern = getWitherPattern();
		BlockPattern.PatternHelper match = pattern.match(worldIn, pos);

		if (match == null) return;

		for (int i = 0; i < 3; ++i) {
			BlockWorldState blockworldstate = match.translateOffset(i, 0, 0);
			worldIn.setBlockState(blockworldstate.getPos(), blockworldstate.getBlockState().withProperty(BlockSkull.NODROP, Boolean.TRUE), 2);
		}

		for (int j = 0; j < pattern.getPalmLength(); ++j) {
			for (int k = 0; k < pattern.getThumbLength(); ++k) {
				BlockWorldState blockworldstate1 = match.translateOffset(j, k, 0);
				worldIn.setBlockState(blockworldstate1.getPos(), Blocks.air.getDefaultState(), 2);
			}
		}

		BlockPos blockpos = match.translateOffset(1, 0, 0).getPos();
		EntityWither entitywither = new EntityWither(worldIn);
		BlockPos blockpos1 = match.translateOffset(1, 2, 0).getPos();
		entitywither.setLocationAndAngles((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.55D, (double) blockpos1.getZ() + 0.5D,
				match.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F);
		entitywither.renderYawOffset = match.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F;
		entitywither.func_82206_m();

		for (EntityPlayer entityplayer : worldIn.getEntitiesWithinAABB(EntityPlayer.class, entitywither.getEntityBoundingBox().expand(50.0D, 50.0D, 50.0D))) {
			entityplayer.triggerAchievement(AchievementList.spawnWither);
		}

		worldIn.spawnEntityInWorld(entitywither);

		for (int l = 0; l < 120; ++l) {
			worldIn.spawnParticle(EnumParticleTypes.SNOWBALL, (double) blockpos.getX() + worldIn.rand.nextDouble(), (double) (blockpos.getY() - 2) + worldIn.rand.nextDouble() * 3.9D,
					(double) blockpos.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
		}

		for (int i1 = 0; i1 < pattern.getPalmLength(); ++i1) {
			for (int j1 = 0; j1 < pattern.getThumbLength(); ++j1) {
				BlockWorldState blockworldstate2 = match.translateOffset(i1, j1, 0);
				worldIn.notifyNeighborsRespectDebug(blockworldstate2.getPos(), Blocks.air);
			}
		}
	}

	@Override
	protected void onSkullPlaced(World w, BlockPos pos, TileEntitySkull skull) {
		checkWitherSpawn(w, pos, skull);
	}

}
