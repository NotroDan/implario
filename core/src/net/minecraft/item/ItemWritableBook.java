package net.minecraft.item;

import net.minecraft.entity.player.Player;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {

	public ItemWritableBook() {
		this.setMaxStackSize(1);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack stack, World worldIn, Player p) {
		p.openGui(ItemStack.class, stack);
		//		p.displayGUIBook(stack);
		p.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
		return stack;
	}

	/**
	 * this method returns true if the book's NBT Tag List "pages" is valid
	 */
	public static boolean isNBTValid(NBTTagCompound nbt) {
		if (nbt == null) {
			return false;
		}
		if (!nbt.hasKey("pages", 9)) {
			return false;
		}
		NBTTagList nbttaglist = nbt.getTagList("pages", 8);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			String s = nbttaglist.getStringTagAt(i);

			if (s == null) {
				return false;
			}

			if (s.length() > 32767) {
				return false;
			}
		}

		return true;
	}

}
