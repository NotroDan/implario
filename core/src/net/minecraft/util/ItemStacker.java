package net.minecraft.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStacker {
	public static ItemStack createItemStack(int id, int data) {
		try {
			Item i = Item.itemRegistry.getObjectById(id);
			return new ItemStack(i, 1, data);
		} catch (NullPointerException e) {
			Block b = Block.blockRegistry.getObjectById(id);
			return new ItemStack(b, 1, data);
		}
	}
}
