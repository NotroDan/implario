package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.inventory.CreativeTabs;
import net.minecraft.entity.player.Player;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSeeds extends Item {

	private Block crops;

	/**
	 * BlockID of the block the seeds can be planted on.
	 */
	private Block soilBlockID;

	public ItemSeeds(Block crops, Block soil) {
		this.crops = crops;
		this.soilBlockID = soil;
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, Player playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (side != EnumFacing.UP) {
			return false;
		}
		if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
			return false;
		}
		if (worldIn.getBlockState(pos).getBlock() == this.soilBlockID && worldIn.isAirBlock(pos.up())) {
			worldIn.setBlockState(pos.up(), this.crops.getDefaultState());
			--stack.stackSize;
			return true;
		}
		return false;
	}

}
