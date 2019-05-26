package net.minecraft.init;

import net.minecraft.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.resources.Datapack;
import net.minecraft.stats.StatList;
import vanilla.Vanilla;

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

		Datapack.LOADED.add(new Vanilla());

		Block.registerBlocks();
		for (Datapack datapack : Datapack.LOADED) datapack.loadBlocks();
		Block.reloadBlockStates();
		Blocks.reload();

		BlockFire.init();

		Item.registerItems();
		for (Datapack datapack : Datapack.LOADED) datapack.loadItems();
		Items.reload();

		StatList.init();
		Enchantments.protection.getClass().getCanonicalName();
		// ToDo: Datapack preinit
		for (Datapack datapack : Datapack.LOADED) datapack.preinit();
		if (!Datapack.LOADED.isEmpty()) {
			Blocks.reload();
			Block.reloadBlockStates();
		}
	}

	public static void print(String text) {
		SYSOUT.println(text);
	}
}
