package vanilla.world;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.logging.IProfiler;
import net.minecraft.network.protocol.minecraft_47.play.server.S2BPacketChangeGameState;
import net.minecraft.network.protocol.minecraft_47.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.chunk.ChunkProviderServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import vanilla.entity.EnumCreatureType;
import vanilla.entity.INpc;
import vanilla.entity.passive.EntityAnimal;
import vanilla.entity.passive.EntityWaterMob;
import vanilla.world.gen.feature.WorldGeneratorBonusChest;
import vanilla.world.gen.feature.village.VillageCollection;
import vanilla.world.gen.feature.village.VillageSiege;
import vanilla.world.gen.provider.VanillaChunkProvider;

import java.util.Arrays;
import java.util.List;

public class VanillaWorldServer extends WorldServer {

	private static final List<WeightedRandomChestContent> bonusChestContent = Arrays.asList(
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
	public VillageCollection villageCollection;
	public VillageSiege villageSiege;
	private SpawnerAnimals spawnerAnimals = new SpawnerAnimals();

	public VanillaWorldServer(MinecraftServer server, ISaveHandler saver, WorldInfo info, int dim, IProfiler profiler) {
		super(server, saver, info, dim, profiler);
		this.dimensionTransfer = new Teleporter(this);
	}

	@Override
	public World init() {
		super.init();

		this.villageSiege = new VillageSiege(this);

		String s = VillageCollection.fileNameForProvider(provider);
		VillageCollection cached = (VillageCollection) getMapStorage().loadData(VillageCollection.class, s);

		if (cached != null) {
			this.villageCollection = cached;
			cached.setWorldsForAll(this);
		} else {
			VillageCollection v = new VillageCollection(this);
			this.villageCollection = v;
			getMapStorage().setData(s, v);
		}

		System.out.println("Инициализация успешно завершена.");

		return this;
	}

	@Override
	public void tick() {
		super.tick();


		if (this.areAllPlayersAsleep()) {
			if (this.getGameRules().getBoolean("doDaylightCycle")) {
				long i = this.worldInfo.getWorldTime() + 24000L;
				this.worldInfo.setWorldTime(i - i % 24000L);
			}

			this.wakeAllPlayers();
		}


		this.theProfiler.startSection("portalForcer");
		((Teleporter) this.dimensionTransfer).removeStalePortalLocations(this.getTotalWorldTime());


		this.theProfiler.endStartSection("mobSpawner");

		if (getGameRules().getBoolean("doMobSpawning") && getWorldInfo().getTerrainType().doMobSpawning()) {
			spawnerAnimals.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
		}


		this.theProfiler.endStartSection("village");
		this.villageCollection.tick();
		this.villageSiege.tick();
		this.theProfiler.endSection();


	}

	public SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos) {
		if (isBasedOnNonVanilla) return null;
		List<SpawnListEntry> list = getActualProvider().getPossibleCreatures(creatureType, pos);
		return list != null && !list.isEmpty() ? WeightedRandom.getRandomItem(this.rand, list) : null;
	}

	public VanillaChunkProvider getActualProvider() {
		return (VanillaChunkProvider) ((ChunkProviderServer) getChunkProvider()).getBase();
	}

	/**
	 * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
	 */
	protected ChunkProviderServer createChunkProvider() {
		ChunkProviderServer p = super.createChunkProvider();
		this.isBasedOnNonVanilla = !(p.getBase() instanceof VanillaChunkProvider);
		return p;
	}

	private boolean isBasedOnNonVanilla;

	public boolean canCreatureTypeSpawnHere(EnumCreatureType creatureType, SpawnListEntry spawnListEntry, BlockPos pos) {
		if (isBasedOnNonVanilla) return false;
		List<SpawnListEntry> list = getActualProvider().getPossibleCreatures(creatureType, pos);
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

			for (Player entityplayer : this.playerEntities) {
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

		for (Player entityplayer : this.playerEntities) {
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

	@Override
	public boolean areAllPlayersAsleep() {
		if (this.allPlayersSleeping && !this.isClientSide) {
			for (Player entityplayer : this.playerEntities) {
				if (entityplayer.isSpectator() || !entityplayer.isPlayerFullyAsleep()) {
					return false;
				}
			}

			return true;
		}
		return false;
	}


	/**
	 * adds a lightning bolt to the list of lightning bolts in this world.
	 */
	public boolean addWeatherEffect(Entity entityIn) {
		if (super.addWeatherEffect(entityIn)) {
			this.mcServer.getConfigurationManager().sendToAllNear(entityIn.posX, entityIn.posY, entityIn.posZ, 512.0D, this.provider.getDimensionId(), new S2CPacketSpawnGlobalEntity(entityIn));
			return true;
		}
		return false;
	}


	/**
	 * Updates all weather states.
	 */
	protected void updateWeather() {
		boolean flag = this.isRaining();
		super.updateWeather();

		if (this.prevRainingStrength != this.rainingStrength) {
			this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.getDimensionId());
		}

		if (this.prevThunderingStrength != this.thunderingStrength) {
			this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.getDimensionId());
		}

		if (flag != this.isRaining()) {
			if (flag) {
				this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
			} else {
				this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(1, 0.0F));
			}

			this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, this.rainingStrength));
			this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, this.thunderingStrength));
		}
	}


	/**
	 * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
	 * Args: entity, forceUpdate
	 */
	public void updateEntityWithOptionalForce(Entity entity, boolean forceUpdate) {
		if (!mcServer.getCanSpawnAnimals() && (entity instanceof EntityAnimal || entity instanceof EntityWaterMob)) {
			entity.setDead();
		}

		if (!mcServer.getCanSpawnNPCs() && entity instanceof INpc) {
			entity.setDead();
		}

		super.updateEntityWithOptionalForce(entity, forceUpdate);
	}

	/**
	 * Creates the bonus chest in the world.
	 */
	@Override
	protected void grantStarterKit() {
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
