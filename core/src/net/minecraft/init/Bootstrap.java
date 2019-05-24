package net.minecraft.init;

import net.minecraft.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;

import java.io.PrintStream;

public class Bootstrap {
	private static final PrintStream SYSOUT = System.out;

	private static boolean alreadyRegistered = false;
	private static final Logger LOGGER = Logger.getInstance();

	/**
	 * Is Bootstrap registration already done?
	 */
	public static boolean isRegistered() {
		return alreadyRegistered;
	}

	/**
	 * Registers blocks, items, stats, etc.
	 */
	public static void register() {
		if (alreadyRegistered) return;

		alreadyRegistered = true;

		Block.registerBlocks();
		BlockFire.init();
		Item.registerItems();
		StatList.init();
		Enchantments.protection.getClass().getCanonicalName();
		// ToDo: Datapack preinit
	}

	public static void print(String text) {
		SYSOUT.println(text);
	}
}
