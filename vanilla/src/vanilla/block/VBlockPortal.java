package vanilla.block;

import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.logging.Log;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import vanilla.item.ItemMonsterPlacer;

import java.util.Random;

public class VBlockPortal extends BlockPortal {

	public VBlockPortal() {
		super();
		setTickRandomly(true);
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if (worldIn.provider.isSurfaceWorld() && worldIn.getGameRules().getBoolean("doMobSpawning") && rand.nextInt(2000) < worldIn.getDifficulty().getDifficultyId()) {
			int i = pos.getY();

			BlockPos blockpos = pos;
			while (!World.doesBlockHaveSolidTopSurface(worldIn, blockpos) && blockpos.getY() > 0) blockpos = blockpos.down();

			if (i > 0 && !worldIn.getBlockState(blockpos.up()).getBlock().isNormalCube()) {
				Entity entity = ItemMonsterPlacer.spawnCreature(worldIn, 57, (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 1.1D, (double) blockpos.getZ() + 0.5D);

				if (entity != null) entity.timeUntilPortal = entity.getPortalCooldown();
			}
		}
	}

}
