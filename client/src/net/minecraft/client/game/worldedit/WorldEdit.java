package net.minecraft.client.game.worldedit;

import net.minecraft.client.MC;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos;

public class WorldEdit {

	private static BlockPos pos1, pos2;

	public static void setPos1(BlockPos pos1) {
		WorldEdit.pos1 = pos1;
	}

	public static void setPos2(BlockPos pos2) {
		WorldEdit.pos2 = pos2;
	}

	public static BlockPos getPos1() {
		return pos1;
	}

	public static BlockPos getPos2() {
		return pos2;
	}

	public static boolean isWandInHand() {
		InventoryPlayer inv = MC.getPlayer().inventory;
		return inv.getHeldItem() != null && inv.getHeldItem().getItem() == Items.blaze_rod;
	}

	public static boolean leftClick(BlockPos pos) {
		if (!isWandInHand()) return false;
		WorldEdit.setPos1(pos);
		return true;
	}

	public static boolean rightClick(BlockPos pos) {
		if (!isWandInHand()) return false;
		WorldEdit.setPos2(pos);
		return true;
	}

}
