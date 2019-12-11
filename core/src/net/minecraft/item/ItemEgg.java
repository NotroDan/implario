package net.minecraft.item;

import net.minecraft.inventory.CreativeTabs;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemEgg extends Item {

	public ItemEgg() {
		this.maxStackSize = 16;
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, Player playerIn) {
		if (!playerIn.capabilities.isCreativeMode) {
			--itemStackIn.stackSize;
		}

		worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!worldIn.isClientSide) {
			worldIn.spawnEntityInWorld(new EntityEgg(worldIn, playerIn));
		}

		playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
		return itemStackIn;
	}

}
