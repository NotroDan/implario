package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.chat.ChatComponentTranslation;

import java.util.*;

public class StatList {

	protected static Map<String, StatBase> oneShotStats = new HashMap<>();

	public static List<StatBase> allStats = new ArrayList<>();
	public static List<StatBase> generalStats = new ArrayList<>();
	public static List<StatCrafting> itemStats = new ArrayList<>();
	public static List<StatCrafting> objectMineStats = new ArrayList<>();

	public static StatBase

			minutesPlayedStat = new StatBasic("stat.playOneMinute", StatFormatter.timeFormat).indepenpent().registerStat(),
			timeSinceDeathStat = new StatBasic("stat.timeSinceDeath", StatFormatter.timeFormat).indepenpent().registerStat(),

	distanceDoveStat = new StatBasic("stat.diveOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceWalkedStat = new StatBasic("stat.walkOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceCrouchedStat = new StatBasic("stat.crouchOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceSprintedStat = new StatBasic("stat.sprintOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceSwumStat = new StatBasic("stat.swimOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceFallenStat = new StatBasic("stat.fallOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceClimbedStat = new StatBasic("stat.climbOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceFlownStat = new StatBasic("stat.flyOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceByMinecartStat = new StatBasic("stat.minecartOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceByBoatStat = new StatBasic("stat.boatOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceByPigStat = new StatBasic("stat.pigOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),
			distanceByHorseStat = new StatBasic("stat.horseOneCm", StatFormatter.distanceFormat).indepenpent().registerStat(),

	damageDealtStat = new StatBasic("stat.damageDealt", StatFormatter.damageFormat).registerStat(),
			damageTakenStat = new StatBasic("stat.damageTaken", StatFormatter.damageFormat).registerStat(),

	leaveGameStat = new StatBasic("stat.leaveGame").indepenpent().registerStat(),
			jumpStat = new StatBasic("stat.jump").indepenpent().registerStat(),
			dropStat = new StatBasic("stat.drop").indepenpent().registerStat(),
			deathsStat = new StatBasic("stat.deaths").registerStat(),
			mobKillsStat = new StatBasic("stat.mobKills").registerStat(),
			animalsBredStat = new StatBasic("stat.animalsBred").registerStat(),
			playerKillsStat = new StatBasic("stat.playerKills").registerStat(),
			fishCaughtStat = new StatBasic("stat.fishCaught").registerStat(),
			junkFishedStat = new StatBasic("stat.junkFished").registerStat(),
			treasureFishedStat = new StatBasic("stat.treasureFished").registerStat(),
			timesTalkedToVillagerStat = new StatBasic("stat.talkedToVillager").registerStat(),
			timesTradedWithVillagerStat = new StatBasic("stat.tradedWithVillager").registerStat(),
			cakeSlicesEatenStat = new StatBasic("stat.cakeSlicesEaten").registerStat(),
			cauldronFilledStat = new StatBasic("stat.cauldronFilled").registerStat(),
			cauldronUsedStat = new StatBasic("stat.cauldronUsed").registerStat(),
			armorCleanedStat = new StatBasic("stat.armorCleaned").registerStat(),
			bannerCleanedStat = new StatBasic("stat.bannerCleaned").registerStat(),
			brewingsOpenedStat = new StatBasic("stat.brewingstandInteraction").registerStat(),
			beaconsOpenedStat = new StatBasic("stat.beaconInteraction").registerStat(),
			droppersOpenedStat = new StatBasic("stat.dropperInspected").registerStat(),
			hoppersOpenedStat = new StatBasic("stat.hopperInspected").registerStat(),
			dispensersOpenedStat = new StatBasic("stat.dispenserInspected").registerStat(),
			noteblockPlayedStat = new StatBasic("stat.noteblockPlayed").registerStat(),
			noteblockTunedStat = new StatBasic("stat.noteblockTuned").registerStat(),
			flowerPottedStat = new StatBasic("stat.flowerPotted").registerStat(),
			trappedChestTriggeredStat = new StatBasic("stat.trappedChestTriggered").registerStat(),
			enderChestOpenedStat = new StatBasic("stat.enderchestOpened").registerStat(),
			itemsEnchantedStat = new StatBasic("stat.itemEnchanted").registerStat(),
			recordsPlayedStat = new StatBasic("stat.recordPlayed").registerStat(),
			furnacesOpenedStat = new StatBasic("stat.furnaceInteraction").registerStat(),
			craftingTableOpenedStat = new StatBasic("stat.workbenchInteraction").registerStat(),
			chestsOpenedStat = new StatBasic("stat.chestOpened").registerStat();


	public static final StatBase[] mineBlockStatArray = new StatBase[4096];

	/**
	 * Tracks the number of items a given block or item has been crafted.
	 */
	public static final StatBase[] objectCraftStats = new StatBase[32000];

	/**
	 * Tracks the number of times a given block or item has been used.
	 */
	public static final StatBase[] objectUseStats = new StatBase[32000];

	/**
	 * Tracks the number of times a given block or item has been broken.
	 */
	public static final StatBase[] objectBreakStats = new StatBase[32000];

	public static void init() {
		initMiningStats();
		initStats();
		initItemDepleteStats();
		initCraftableStats();
		AchievementList.init();
	}

	/**
	 * Initializes statistics related to craftable items. Is only called after both block and item stats have been
	 * initialized.
	 */
	private static void initCraftableStats() {
		Set<Item> set = new HashSet<>();

		for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
			if (irecipe.getRecipeOutput() != null) {
				set.add(irecipe.getRecipeOutput().getItem());
			}
		}

		for (ItemStack itemstack : FurnaceRecipes.instance().getSmeltingList().values()) {
			set.add(itemstack.getItem());
		}

		for (Item item : set) {
			if (item != null) {
				int i = Item.getIdFromItem(item);
				String s = func_180204_a(item);

				if (s != null) {
					objectCraftStats[i] = new StatCrafting("stat.craftItem.", s, new ChatComponentTranslation("stat.craftItem", new ItemStack(item).getChatComponent()), item).registerStat();
				}
			}
		}

		replaceAllSimilarBlocks(objectCraftStats);
	}

	private static void initMiningStats() {
		for (Block block : Block.blockRegistry) {
			Item item = Item.getItemFromBlock(block);

			if (item != null) {
				int i = Block.getIdFromBlock(block);
				String s = func_180204_a(item);

				if (s != null && block.getEnableStats()) {
					mineBlockStatArray[i] = new StatCrafting("stat.mineBlock.", s, new ChatComponentTranslation("stat.mineBlock", new ItemStack(block).getChatComponent()), item).registerStat();
					objectMineStats.add((StatCrafting) mineBlockStatArray[i]);
				}
			}
		}

		replaceAllSimilarBlocks(mineBlockStatArray);
	}

	private static void initStats() {
		for (Item item : Item.itemRegistry) {
			if (item != null) {
				int i = Item.getIdFromItem(item);
				String s = func_180204_a(item);

				if (s != null) {
					objectUseStats[i] = new StatCrafting("stat.useItem.", s, new ChatComponentTranslation("stat.useItem", new ItemStack(item).getChatComponent()), item).registerStat();

					if (!(item instanceof ItemBlock)) {
						itemStats.add((StatCrafting) objectUseStats[i]);
					}
				}
			}
		}

		replaceAllSimilarBlocks(objectUseStats);
	}

	private static void initItemDepleteStats() {
		for (Item item : Item.itemRegistry) {
			if (item != null) {
				int i = Item.getIdFromItem(item);
				String s = func_180204_a(item);

				if (s != null && item.isDamageable()) {
					objectBreakStats[i] = new StatCrafting("stat.breakItem.", s, new ChatComponentTranslation("stat.breakItem", new ItemStack(item).getChatComponent()), item).registerStat();
				}
			}
		}

		replaceAllSimilarBlocks(objectBreakStats);
	}

	private static String func_180204_a(Item p_180204_0_) {
		ResourceLocation resourcelocation = Item.itemRegistry.getNameForObject(p_180204_0_);
		return resourcelocation != null ? resourcelocation.toString().replace(':', '.') : null;
	}

	/**
	 * Forces all dual blocks to count for each other on the stats list
	 */
	private static void replaceAllSimilarBlocks(StatBase[] p_75924_0_) {
		mergeStatBases(p_75924_0_, Blocks.water, Blocks.flowing_water);
		mergeStatBases(p_75924_0_, Blocks.lava, Blocks.flowing_lava);
		mergeStatBases(p_75924_0_, Blocks.lit_pumpkin, Blocks.pumpkin);
		mergeStatBases(p_75924_0_, Blocks.lit_furnace, Blocks.furnace);
		mergeStatBases(p_75924_0_, Blocks.lit_redstone_ore, Blocks.redstone_ore);
		mergeStatBases(p_75924_0_, Blocks.powered_repeater, Blocks.unpowered_repeater);
		mergeStatBases(p_75924_0_, Blocks.powered_comparator, Blocks.unpowered_comparator);
		mergeStatBases(p_75924_0_, Blocks.redstone_torch, Blocks.unlit_redstone_torch);
		mergeStatBases(p_75924_0_, Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
		mergeStatBases(p_75924_0_, Blocks.double_stone_slab, Blocks.stone_slab);
		mergeStatBases(p_75924_0_, Blocks.double_wooden_slab, Blocks.wooden_slab);
		mergeStatBases(p_75924_0_, Blocks.double_stone_slab2, Blocks.stone_slab2);
		mergeStatBases(p_75924_0_, Blocks.grass, Blocks.dirt);
		mergeStatBases(p_75924_0_, Blocks.farmland, Blocks.dirt);
	}

	/**
	 * Merge {@link StatBase} object references for similar blocks
	 */
	private static void mergeStatBases(StatBase[] statBaseIn, Block a, Block b) {
		int i = Block.getIdFromBlock(a);
		int j = Block.getIdFromBlock(b);

		if (statBaseIn[i] != null && statBaseIn[j] == null) {
			statBaseIn[j] = statBaseIn[i];
		} else {
			allStats.remove(statBaseIn[i]);
			objectMineStats.remove(statBaseIn[i]);
			generalStats.remove(statBaseIn[i]);
			statBaseIn[i] = statBaseIn[j];
		}
	}

	public static StatBase createStatKillEntity(EntityList.EntityEggInfo eggInfo) {
		String s = EntityList.getStringFromID(eggInfo.spawnedID);
		return s == null ? null : new StatBase("stat.killEntity." + s, new ChatComponentTranslation("stat.entityKill", new ChatComponentTranslation("entity." + s + ".name"))).registerStat();
	}

	public static StatBase createStatEntityKilledBy(EntityList.EntityEggInfo eggInfo) {
		String s = EntityList.getStringFromID(eggInfo.spawnedID);
		return s == null ? null : new StatBase("stat.entityKilledBy." + s, new ChatComponentTranslation("stat.entityKilledBy", new ChatComponentTranslation("entity." + s + ".name"))).registerStat();
	}

	public static StatBase getOneShotStat(String p_151177_0_) {
		return oneShotStats.get(p_151177_0_);
	}

	public static void unregister(StatBase stat) {
		oneShotStats.remove(stat.statId);
		allStats.remove(stat);
	}

}
