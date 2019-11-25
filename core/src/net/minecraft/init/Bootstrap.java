package net.minecraft.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.resources.DatapackManager;
import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.stats.StatList;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

public class Bootstrap {

	private static final PrintStream SYSOUT = System.out;

	private static boolean registered = false, alreadyRegistered;

	/**
	 * Is Bootstrap registration already done?
	 */
	public static boolean isRegistered() {
		return registered;
	}

	/**
	 * Registers blocks, items, stats, etc.
	 */
	public static void register() {
		registered = true;

		Collection<DatapackLoader> datapacks = DatapackManager.getTree().loadingOrder();
		System.out.println(Arrays.toString(datapacks.toArray()));

		Block.registerBlocks();
		for (DatapackLoader loader : datapacks) loader.getInstance().loadBlocks();
		Block.reloadBlockStates();
		Blocks.reload();

		BlockFire.init();

		Item.registerItems();
		for (DatapackLoader loader : datapacks) loader.getInstance().loadItems();
		Items.reload();

		if(!alreadyRegistered){
			StatList.init();
			Enchantments.init();
		}
		for (DatapackLoader loader : datapacks) {
			System.out.println("Преинициализация датапака " + loader.getInstance().getDomain());
			loader.getInstance().preinit();
		}
		if (!datapacks.isEmpty()) {
			Blocks.reload();
			Block.reloadBlockStates();
		}
		alreadyRegistered = true;
	}

	public static void print(String text) {
		SYSOUT.println(text);
	}

}
