package vanilla.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class VItemDye extends ItemDye {


	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) return false;
		EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(stack.getMetadata());

		if (enumdyecolor == EnumDyeColor.WHITE) {
			if (applyBonemeal(stack, worldIn, pos)) {
				if (!worldIn.isClientSide) worldIn.playAuxSFX(2005, pos, 0);
				return true;
			}
		} else if (enumdyecolor == EnumDyeColor.BROWN) {
			IBlockState iblockstate = worldIn.getBlockState(pos);
			Block block = iblockstate.getBlock();

			if (block == Blocks.log && iblockstate.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE) {
				if (side == EnumFacing.DOWN) {
					return false;
				}

				if (side == EnumFacing.UP) {
					return false;
				}

				pos = pos.offset(side);

				if (worldIn.isAirBlock(pos)) {
					IBlockState iblockstate1 = Blocks.cocoa.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
					worldIn.setBlockState(pos, iblockstate1, 2);

					if (!playerIn.capabilities.isCreativeMode) {
						--stack.stackSize;
					}
				}

				return true;
			}
		}

		return false;
	}

	public static boolean applyBonemeal(ItemStack item, World w, BlockPos target) {

		IBlockState iblockstate = w.getBlockState(target);

		if (!(iblockstate.getBlock() instanceof IGrowable)) return false;

		IGrowable g = (IGrowable) iblockstate.getBlock();

		if (!g.canGrow(w, target, iblockstate, w.isClientSide)) return false;
		if (w.isClientSide) return true;

		if (g.canUseBonemeal(w, w.rand, target, iblockstate))
			g.grow(w, w.rand, target, iblockstate);

		--item.stackSize;

		return true;
	}

}
