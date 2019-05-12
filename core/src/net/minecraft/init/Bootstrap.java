package net.minecraft.init;

import net.minecraft.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.util.LoggingPrintStream;

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
		// ToDo: Datapack preinit
	}

	/**
	 * redirect standard streams to logger
	 */
	private static void redirectOutputToLog() {
		System.setErr(new LoggingPrintStream("STDERR", System.err));
		System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
	}

	public static void print(String text) {
		SYSOUT.println(text);
	}
}
