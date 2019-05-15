package net.minecraft.world;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.SpawnListEntry;

import java.util.List;

public class VanillaWorldServer extends WorldServer {


	private static final List<WeightedRandomChestContent> bonusChestContent = Lists.newArrayList(
			new WeightedRandomChestContent(Items.stick, 0, 1, 3, 10),
			new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.planks), 0, 1, 3, 10),
			new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log), 0, 1, 3, 10), new WeightedRandomChestContent(Items.stone_axe, 0, 1, 1, 3),
			new WeightedRandomChestContent(Items.wooden_axe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.stone_pickaxe, 0, 1, 1, 3),
			new WeightedRandomChestContent(Items.wooden_pickaxe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.apple, 0, 2, 3, 5), new WeightedRandomChestContent(Items.bread, 0, 2, 3, 3),
			new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log2), 0, 1, 3, 10));



	/**
	 * is false if there are no players
	 */
	private boolean allPlayersSleeping;



	public SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos) {
		List<SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
		return list != null && !list.isEmpty() ? WeightedRandom.getRandomItem(this.rand, list) : null;
	}

	public boolean canCreatureTypeSpawnHere(EnumCreatureType creatureType, SpawnListEntry spawnListEntry, BlockPos pos) {
		List<SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
		return list != null && !list.isEmpty() && list.contains(spawnListEntry);
	}



	/**
	 * Updates the flag that indicates whether or not all players in the world are sleeping.
	 */
	public void updateAllPlayersSleepingFlag() {
		this.allPlayersSleeping = false;

		if (!this.playerEntities.isEmpty()) {
			int i = 0;
			int j = 0;

			for (EntityPlayer entityplayer : this.playerEntities) {
				if (entityplayer.isSpectator()) {
					++i;
				} else if (entityplayer.isPlayerSleeping()) {
					++j;
				}
			}

			this.allPlayersSleeping = j > 0 && j >= this.playerEntities.size() - i;
		}
	}

	protected void wakeAllPlayers() {
		this.allPlayersSleeping = false;

		for (EntityPlayer entityplayer : this.playerEntities) {
			if (entityplayer.isPlayerSleeping()) {
				entityplayer.wakeUpPlayer(false, false, true);
			}
		}

		this.resetRainAndThunder();
	}



	private void resetRainAndThunder() {
		this.worldInfo.setRainTime(0);
		this.worldInfo.setRaining(false);
		this.worldInfo.setThunderTime(0);
		this.worldInfo.setThundering(false);
	}


	public boolean areAllPlayersAsleep() {
		if (this.allPlayersSleeping && !this.isClientSide) {
			for (EntityPlayer entityplayer : this.playerEntities) {
				if (entityplayer.isSpectator() || !entityplayer.isPlayerFullyAsleep()) {
					return false;
				}
			}

			return true;
		}
		return false;
	}


	/**
	 * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
	 * Args: entity, forceUpdate
	 */
	public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
		if (!this.canSpawnAnimals() && (entityIn instanceof EntityAnimal || entityIn instanceof EntityWaterMob)) {
			entityIn.setDead();
		}

		if (!this.canSpawnNPCs() && entityIn instanceof INpc) {
			entityIn.setDead();
		}

		super.updateEntityWithOptionalForce(entityIn, forceUpdate);
	}


	/**
	 * Creates the bonus chest in the world.
	 */
	protected void createBonusChest() {
		WorldGeneratorBonusChest worldgeneratorbonuschest = new WorldGeneratorBonusChest(bonusChestContent, 10);

		for (int i = 0; i < 10; ++i) {
			int j = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
			int k = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
			BlockPos blockpos = this.getTopSolidOrLiquidBlock(new BlockPos(j, 0, k)).up();

			if (worldgeneratorbonuschest.generate(this, this.rand, blockpos)) {
				break;
			}
		}
	}


}
