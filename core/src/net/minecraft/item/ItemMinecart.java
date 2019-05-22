package net.minecraft.item;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.creativetab.CreativeTabs;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMinecart extends Item {

	public final EntityMinecart.EnumMinecartType minecartType;

	public ItemMinecart(EntityMinecart.EnumMinecartType type) {
		this.maxStackSize = 1;
		this.minecartType = type;
		this.setCreativeTab(CreativeTabs.tabTransport);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if (BlockRailBase.isRailBlock(iblockstate)) {
			if (!worldIn.isClientSide) {
				BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection) iblockstate.getValue(
						((BlockRailBase) iblockstate.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
				double d0 = 0.0D;

				if (blockrailbase$enumraildirection.isAscending()) {
					d0 = 0.5D;
				}

				EntityMinecart entityminecart = EntityMinecart.func_180458_a(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.0625D + d0, (double) pos.getZ() + 0.5D, this.minecartType);

				if (stack.hasDisplayName()) {
					entityminecart.setCustomNameTag(stack.getDisplayName());
				}

				worldIn.spawnEntityInWorld(entityminecart);
			}

			--stack.stackSize;
			return true;
		}
		return false;
	}

}
