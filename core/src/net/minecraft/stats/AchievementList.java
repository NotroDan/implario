package net.minecraft.stats;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonSerializableSet;

import java.util.List;

public class AchievementList {

	/**
	 * Is the smallest column used to display a achievement on the GUI.
	 */
	public static int minDisplayColumn;

	/**
	 * Is the smallest row used to display a achievement on the GUI.
	 */
	public static int minDisplayRow;

	/**
	 * Is the biggest column used to display a achievement on the GUI.
	 */
	public static int maxDisplayColumn;

	/**
	 * Is the biggest row used to display a achievement on the GUI.
	 */
	public static int maxDisplayRow;
	public static List<Achievement> achievementList = new java.util.ArrayList<>();

	public static Achievement
			openInventory      = new Achievement("openInventory",      0, 0, Items.book, null).indepenpent().registerStat(),
			mineWood           = new Achievement("mineWood",           2, 1, Blocks.log, openInventory).registerStat(),
			buildWorkBench     = new Achievement("buildWorkBench",     4, -1, Blocks.crafting_table, mineWood).registerStat(),
			buildPickaxe       = new Achievement("buildPickaxe",       4, 2, Items.wooden_pickaxe, buildWorkBench).registerStat(),
			buildFurnace       = new Achievement("buildFurnace",       3, 4, Blocks.furnace, buildPickaxe).registerStat(),
			acquireIron        = new Achievement("acquireIron",        1, 4, Items.iron_ingot, buildFurnace).registerStat(),
			buildHoe           = new Achievement("buildHoe",           2, -3, Items.wooden_hoe, buildWorkBench).registerStat(),
			makeBread          = new Achievement("makeBread",          -1, -3, Items.bread, buildHoe).registerStat(),
			bakeCake           = new Achievement("bakeCake",           0, -5, Items.cake, buildHoe).registerStat(),
			buildBetterPickaxe = new Achievement("buildBetterPickaxe", 6, 2, Items.stone_pickaxe, buildPickaxe).registerStat(),
			cookFish           = new Achievement("cookFish",           2, 6, Items.cooked_fish, buildFurnace).registerStat(),
			onARail            = new Achievement("onARail",            2, 3, Blocks.rail, acquireIron).setSpecial().registerStat(),
			buildSword         = new Achievement("buildSword",         6, -1, Items.wooden_sword, buildWorkBench).registerStat(),
			killEnemy          = new Achievement("killEnemy",          8, -1, Items.bone, buildSword).registerStat(),
			killCow            = new Achievement("killCow",            7, -3, Items.leather, buildSword).registerStat(),
			flyPig             = new Achievement("flyPig",             9, -3, Items.porkchop, killCow).setSpecial().registerStat(),
			snipeSkeleton      = new Achievement("snipeSkeleton",      7, 0, Items.bow, killEnemy).setSpecial().registerStat(),
			diamonds           = new Achievement("diamonds",           -1, 5, Blocks.diamond_ore, acquireIron).registerStat(),
			diamondsToYou      = new Achievement("diamondsToYou",      -1, 2, Items.diamond, diamonds).registerStat(),
			portal             = new Achievement("portal",             -1, 7, Blocks.obsidian, diamonds).registerStat(),
			ghast              = new Achievement("ghast",              4, 8, Items.ghast_tear, portal).setSpecial().registerStat(),
			blazeRod           = new Achievement("blazeRod",           0, 9, Items.blaze_rod, portal).registerStat(),
			potion             = new Achievement("potion",             2, 8, Items.potionitem, blazeRod).registerStat(),
			theEnd             = new Achievement("theEnd",             3, 10, Items.ender_eye, blazeRod).setSpecial().registerStat(),
			theEnd2            = new Achievement("theEnd2",            4, 13, Blocks.dragon_egg, theEnd).setSpecial().registerStat(),
			enchantments       = new Achievement("enchantments",       -4, 4, Blocks.enchanting_table, diamonds).registerStat(),
			overkill           = new Achievement("overkill",           -4, 1, Items.diamond_sword, enchantments).setSpecial().registerStat(),
			bookcase           = new Achievement("bookcase",           -3, 6, Blocks.bookshelf, enchantments).registerStat(),
			breedCow           = new Achievement("breedCow",           7, -5, Items.wheat, killCow).registerStat(),
			spawnWither        = new Achievement("spawnWither",        7, 12, new ItemStack(Items.skull, 1, 1), theEnd2).registerStat(),
			killWither         = new Achievement("killWither",         7, 10, Items.nether_star, spawnWither).registerStat(),
			fullBeacon         = new Achievement("fullBeacon",         7, 8, Blocks.beacon, killWither).setSpecial().registerStat(),
			exploreAllBiomes   = new Achievement("exploreAllBiomes",   4, 8, Items.diamond_boots, theEnd).serializer(JsonSerializableSet.class).setSpecial().registerStat(),
			overpowered        = new Achievement("overpowered",        6, 4, new ItemStack(Items.golden_apple, 1, 1), buildBetterPickaxe).setSpecial().registerStat();

	/**
	 * A stub functions called to make the static initializer for this class run.
	 */
	public static void init() {
	}

}
