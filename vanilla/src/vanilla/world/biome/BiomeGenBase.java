package vanilla.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.Logger;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import vanilla.entity.EnumCreatureType;
import vanilla.Vanilla;
import vanilla.entity.monster.*;
import vanilla.entity.passive.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.biome.TempCategory;
import net.minecraft.world.chunk.ChunkPrimer;
import vanilla.world.gen.NoiseGeneratorPerlin;
import vanilla.world.gen.feature.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings ("StaticInitializerReferencesSubClass")
public abstract class BiomeGenBase extends Biome {

	private static final Logger logger = Logger.getInstance();
	protected static final BiomeGenBase.Height height_Default = new BiomeGenBase.Height(0.1F, 0.2F);
	protected static final BiomeGenBase.Height height_ShallowWaters = new BiomeGenBase.Height(-0.5F, 0.0F);
	protected static final BiomeGenBase.Height height_Oceans = new BiomeGenBase.Height(-1.0F, 0.1F);
	protected static final BiomeGenBase.Height height_DeepOceans = new BiomeGenBase.Height(-1.8F, 0.1F);
	protected static final BiomeGenBase.Height height_LowPlains = new BiomeGenBase.Height(0.125F, 0.05F);
	protected static final BiomeGenBase.Height height_MidPlains = new BiomeGenBase.Height(0.2F, 0.2F);
	protected static final BiomeGenBase.Height height_LowHills = new BiomeGenBase.Height(0.45F, 0.3F);
	protected static final BiomeGenBase.Height height_HighPlateaus = new BiomeGenBase.Height(1.5F, 0.025F);
	protected static final BiomeGenBase.Height height_MidHills = new BiomeGenBase.Height(1.0F, 0.5F);
	protected static final BiomeGenBase.Height height_Shores = new BiomeGenBase.Height(0.0F, 0.025F);
	protected static final BiomeGenBase.Height height_RockyWaters = new BiomeGenBase.Height(0.1F, 0.8F);
	protected static final BiomeGenBase.Height height_LowIslands = new BiomeGenBase.Height(0.2F, 0.3F);
	protected static final BiomeGenBase.Height height_PartiallySubmerged = new BiomeGenBase.Height(-0.2F, 0.1F);

	public static final Map<String, BiomeGenBase> BIOME_ID_MAP = Maps.newHashMap();
	public static final BiomeGenBase
			ocean = new BiomeGenOcean(0, "Ocean").setColor(0x000070).setHeight(height_Oceans),
			plains = new BiomeGenPlains(1, "Plains").setColor(0x8db360),
			desert = new BiomeGenDesert(2, "Desert").setColor(0xfa9418).setDisableRain().setTemperatureRainfall(2.0F, 0.0F).setHeight(height_LowPlains),
			extremeHills = new BiomeGenHills(3, "Extreme Hills", false).setColor(0x606060).setHeight(height_MidHills).setTemperatureRainfall(0.2F, 0.3F),
			forest = new BiomeGenForest(4, "Forest", 0).setColor(353825),
			taiga = new BiomeGenTaiga(5, "Taiga", 0).setColor(747097).setFillerBlockMetadata(5159473).setTemperatureRainfall(0.25F, 0.8F).setHeight(height_MidPlains),
			swampland = new BiomeGenSwamp(6, "Swampland").setColor(522674).setFillerBlockMetadata(9154376).setHeight(height_PartiallySubmerged).setTemperatureRainfall(0.8F, 0.9F),
			river = new BiomeGenRiver(7, "River").setColor(255).setHeight(height_ShallowWaters),
			hell = new BiomeGenHell(8, "Hell").setColor(16711680).setDisableRain().setTemperatureRainfall(2.0F, 0.0F),
			end = new BiomeGenEnd(9, "The End").setColor(8421631).setDisableRain(),
			frozenOcean = new BiomeGenOcean(10, "FrozenOcean").setColor(9474208).setEnableSnow().setHeight(height_Oceans).setTemperatureRainfall(0.0F, 0.5F),
			frozenRiver = new BiomeGenRiver(11, "FrozenRiver").setColor(10526975).setEnableSnow().setHeight(height_ShallowWaters).setTemperatureRainfall(0.0F, 0.5F),
			icePlains = new BiomeGenSnow(12, "Ice Plains", false).setColor(16777215).setEnableSnow().setTemperatureRainfall(0.0F, 0.5F).setHeight(height_LowPlains),
			iceMountains = new BiomeGenSnow(13, "Ice Mountains", false).setColor(10526880).setEnableSnow().setHeight(height_LowHills).setTemperatureRainfall(0.0F, 0.5F),
			mushroomIsland = new BiomeGenMushroomIsland(14, "MushroomIsland").setColor(16711935).setTemperatureRainfall(0.9F, 1.0F).setHeight(height_LowIslands),
			mushroomIslandShore = new BiomeGenMushroomIsland(15, "MushroomIslandShore").setColor(10486015).setTemperatureRainfall(0.9F, 1.0F).setHeight(height_Shores),
			beach = new BiomeGenBeach(16, "Beach").setColor(16440917).setTemperatureRainfall(0.8F, 0.4F).setHeight(height_Shores),
			desertHills = new BiomeGenDesert(17, "DesertHills").setColor(13786898).setDisableRain().setTemperatureRainfall(2.0F, 0.0F).setHeight(height_LowHills),
			forestHills = new BiomeGenForest(18, "ForestHills", 0).setColor(2250012).setHeight(height_LowHills),
			taigaHills = new BiomeGenTaiga(19, "TaigaHills", 0).setColor(1456435).setFillerBlockMetadata(5159473).setTemperatureRainfall(0.25F, 0.8F).setHeight(height_LowHills),
			extremeHillsEdge = new BiomeGenHills(20, "Extreme Hills Edge", true).setColor(7501978).setHeight(height_MidHills.attenuate()).setTemperatureRainfall(0.2F, 0.3F),
			jungle = new BiomeGenJungle(21, "Jungle", false).setColor(5470985).setFillerBlockMetadata(5470985).setTemperatureRainfall(0.95F, 0.9F),
			jungleHills = new BiomeGenJungle(22, "JungleHills", false).setColor(2900485).setFillerBlockMetadata(5470985).setTemperatureRainfall(0.95F, 0.9F).setHeight(height_LowHills),
			jungleEdge = new BiomeGenJungle(23, "JungleEdge", true).setColor(6458135).setFillerBlockMetadata(5470985).setTemperatureRainfall(0.95F, 0.8F),
			deepOcean = new BiomeGenOcean(24, "Deep Ocean").setColor(48).setHeight(height_DeepOceans),
			stoneBeach = new BiomeGenStoneBeach(25, "Stone Beach").setColor(10658436).setTemperatureRainfall(0.2F, 0.3F).setHeight(height_RockyWaters),
			coldBeach = new BiomeGenBeach(26, "Cold Beach").setColor(16445632).setTemperatureRainfall(0.05F, 0.3F).setHeight(height_Shores).setEnableSnow(),
			birchForest = new BiomeGenForest(27, "Birch Forest", 2).setColor(3175492),
			birchForestHills = new BiomeGenForest(28, "Birch Forest Hills", 2).setColor(2055986).setHeight(height_LowHills),
			roofedForest = new BiomeGenForest(29, "Roofed Forest", 3).setColor(4215066),
			coldTaiga = new BiomeGenTaiga(30, "Cold Taiga", 0).setColor(3233098).setFillerBlockMetadata(5159473).setEnableSnow().setTemperatureRainfall(-0.5F, 0.4F).setHeight(
					height_MidPlains).func_150563_c(16777215),
			coldTaigaHills = new BiomeGenTaiga(31, "Cold Taiga Hills", 0).setColor(2375478).setFillerBlockMetadata(5159473).setEnableSnow().setTemperatureRainfall(-0.5F, 0.4F).setHeight(
					height_LowHills).func_150563_c(16777215),
			megaTaiga = new BiomeGenTaiga(32, "Mega Taiga", 1).setColor(5858897).setFillerBlockMetadata(5159473).setTemperatureRainfall(0.3F, 0.8F).setHeight(height_MidPlains),
			megaTaigaHills = new BiomeGenTaiga(33, "Mega Taiga Hills", 1).setColor(4542270).setFillerBlockMetadata(5159473).setTemperatureRainfall(0.3F, 0.8F).setHeight(height_LowHills),
			extremeHillsPlus = new BiomeGenHills(34, "Extreme Hills+", true).setColor(5271632).setHeight(height_MidHills).setTemperatureRainfall(0.2F, 0.3F),
			savanna = new BiomeGenSavanna(35, "Savanna").setColor(12431967).setTemperatureRainfall(1.2F, 0.0F).setDisableRain().setHeight(height_LowPlains),
			savannaPlateau = new BiomeGenSavanna(36, "Savanna Plateau").setColor(10984804).setTemperatureRainfall(1.0F, 0.0F).setDisableRain().setHeight(height_HighPlateaus),
			mesa = new BiomeGenMesa(37, "Mesa", false, false).setColor(14238997),
			mesaPlateau_F = new BiomeGenMesa(38, "Mesa Plateau F", false, true).setColor(11573093).setHeight(height_HighPlateaus),
			mesaPlateau = new BiomeGenMesa(39, "Mesa Plateau", false, false).setColor(13274213).setHeight(height_HighPlateaus);
	protected static final NoiseGeneratorPerlin temperatureNoise;
	protected static final NoiseGeneratorPerlin GRASS_COLOR_NOISE;
	protected static final WorldGenDoublePlant DOUBLE_PLANT_GENERATOR;
	public int color;
	public int field_150609_ah;

	/**
	 * The block expected to be on the top of this biome
	 */
	public IBlockState topBlock = Blocks.grass.getDefaultState();

	/**
	 * The block to fill spots in when not on the top
	 */
	public IBlockState fillerBlock = Blocks.dirt.getDefaultState();
	public int fillerBlockMetadata = 5169201;

	/**
	 * The minimum height of this biome. Default 0.1.
	 */
	public float minHeight;

	/**
	 * The maximum height of this biome. Default 0.3.
	 */
	public float maxHeight;

	/**
	 * The temperature of this biome.
	 */
	public float temperature;

	/**
	 * The rainfall in this biome.
	 */
	public float rainfall;

	/**
	 * Color tint applied to water depending on biome
	 */
	public int waterColorMultiplier;

	/**
	 * The biome decorator.
	 */
	public BiomeDecorator theBiomeDecorator;
	protected List<SpawnListEntry> spawnableMonsterList;
	protected List<SpawnListEntry> spawnableCreatureList;
	protected List<SpawnListEntry> spawnableWaterCreatureList;
	protected List<SpawnListEntry> spawnableCaveCreatureList;

	/**
	 * Set to true if snow is enabled for this biome.
	 */
	protected boolean enableSnow;

	/**
	 * Is true (default) if the biome support rain (desert and nether can't have rain)
	 */
	protected boolean enableRain;

	/**
	 * The tree generator.
	 */
	protected WorldGenTrees worldGeneratorTrees;

	/**
	 * The big tree generator.
	 */
	protected WorldGenBigTree worldGeneratorBigTree;

	/**
	 * The swamp tree generator.
	 */
	protected WorldGenSwamp worldGeneratorSwamp;

	protected BiomeGenBase(int legacyId, String name) {
		super(legacyId, name, Vanilla.VANILLA);
		this.minHeight = height_Default.rootHeight;
		this.maxHeight = height_Default.variation;
		this.temperature = 0.5F;
		this.rainfall = 0.5F;
		this.waterColorMultiplier = 16777215;
		this.spawnableMonsterList = new ArrayList<>();
		this.spawnableCreatureList = new ArrayList<>();
		this.spawnableWaterCreatureList = new ArrayList<>();
		this.spawnableCaveCreatureList = new ArrayList<>();
		this.enableRain = true;
		this.worldGeneratorTrees = new WorldGenTrees(false);
		this.worldGeneratorBigTree = new WorldGenBigTree(false);
		this.worldGeneratorSwamp = new WorldGenSwamp();
		this.theBiomeDecorator = this.createBiomeDecorator();
		this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 12, 4, 4));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 10, 3, 3));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 10, 4, 4));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 10, 4, 4));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 8, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 100, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 100, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 100, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 100, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 10, 1, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityWitch.class, 5, 1, 1));
		this.spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10, 4, 4));
		this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityBat.class, 10, 8, 8));
	}

	public static BiomeGenBase toGenBase(Biome biome) {
		return biome instanceof BiomeGenBase ? (BiomeGenBase) biome : plains;
	}

	/**
	 * Allocate a new BiomeDecorator for this BiomeGenBase
	 */
	protected BiomeDecorator createBiomeDecorator() {
		return new BiomeDecorator();
	}

	/**
	 * Sets the temperature and rainfall of this biome.
	 */
	protected BiomeGenBase setTemperatureRainfall(float temperatureIn, float rainfallIn) {
		if (temperatureIn > 0.1F && temperatureIn < 0.2F) {
			throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
		}
		this.temperature = temperatureIn;
		this.rainfall = rainfallIn;
		return this;
	}

	protected final BiomeGenBase setHeight(BiomeGenBase.Height heights) {
		this.minHeight = heights.rootHeight;
		this.maxHeight = heights.variation;
		return this;
	}

	/**
	 * Disable the rain for the biome.
	 */
	protected BiomeGenBase setDisableRain() {
		this.enableRain = false;
		return this;
	}

	public WorldGenAbstractTree genBigTreeChance(Random rand) {
		return rand.nextInt(10) == 0 ? this.worldGeneratorBigTree : this.worldGeneratorTrees;
	}

	/**
	 * Gets a WorldGen appropriate for this biome.
	 */
	public WorldGenerator getRandomWorldGenForGrass(Random rand) {
		return new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
	}

	/**
	 * sets enableSnow to true during biome initialization. returns BiomeGenBase.
	 */
	protected BiomeGenBase setEnableSnow() {
		this.enableSnow = true;
		return this;
	}

	protected BiomeGenBase setFillerBlockMetadata(int meta) {
		this.fillerBlockMetadata = meta;
		return this;
	}

	protected BiomeGenBase setColor(int colorIn) {
		this.func_150557_a(colorIn, false);
		return this;
	}

	protected BiomeGenBase func_150563_c(int p_150563_1_) {
		this.field_150609_ah = p_150563_1_;
		return this;
	}

	protected BiomeGenBase func_150557_a(int p_150557_1_, boolean p_150557_2_) {
		this.color = p_150557_1_;

		if (p_150557_2_) {
			this.field_150609_ah = (p_150557_1_ & 16711422) >> 1;
		} else {
			this.field_150609_ah = p_150557_1_;
		}

		return this;
	}


	public List<SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
		switch (creatureType) {
			case MONSTER:
				return this.spawnableMonsterList;

			case CREATURE:
				return this.spawnableCreatureList;

			case WATER_CREATURE:
				return this.spawnableWaterCreatureList;

			case AMBIENT:
				return this.spawnableCaveCreatureList;

			default:
				return Collections.emptyList();
		}
	}

	/**
	 * Returns true if the biome have snowfall instead a normal rain.
	 */
	public boolean getEnableSnow() {
		return this.isSnowyBiome();
	}

	/**
	 * Return true if the biome supports lightning bolt spawn, either by have the bolts enabled and have rain enabled.
	 */
	public boolean canSpawnLightningBolt() {
		return !this.isSnowyBiome() && this.enableRain;
	}

	/**
	 * Checks to see if the rainfall level of the biome is extremely high
	 */
	public boolean isHighHumidity() {
		return this.rainfall > 0.85F;
	}

	/**
	 * Gets an integer representation of this biome's rainfall
	 */
	public final int getIntRainfall() {
		return (int) (this.rainfall * 65536.0F);
	}

	/**
	 * Gets a floating point representation of this biome's rainfall
	 */
	public final float getFloatRainfall() {
		return this.rainfall;
	}

	@Override
	public int getWaterColorMultiplier() {
		return waterColorMultiplier;
	}

	/**
	 * Gets a floating point representation of this biome's temperature
	 */
	public final float getFloatTemperature(BlockPos pos) {
		if (pos.getY() > 64) {
			float f = (float) (temperatureNoise.func_151601_a((double) pos.getX() * 1.0D / 8.0D, (double) pos.getZ() * 1.0D / 8.0D) * 4.0D);
			return this.temperature - (f + (float) pos.getY() - 64.0F) * 0.05F / 30.0F;
		}
		return this.temperature;
	}

	public void decorate(World worldIn, Random rand, BlockPos pos) {
		this.theBiomeDecorator.decorate(worldIn, rand, this, pos);
	}

	public int getGrassColorAtPos(BlockPos pos) {
		double d0 = (double) MathHelper.clamp_float(this.getFloatTemperature(pos), 0.0F, 1.0F);
		double d1 = (double) MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
		return ColorizerGrass.getGrassColor(d0, d1);
	}

	public boolean isSnowyBiome() {
		return this.enableSnow;
	}

	public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int p_180622_4_, int p_180622_5_, double p_180622_6_) {
		this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, p_180622_4_, p_180622_5_, p_180622_6_);
	}

	public final void generateBiomeTerrain(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int p_180628_4_, int p_180628_5_, double p_180628_6_) {
		int i = worldIn.getSeaLevel();
		IBlockState iblockstate = this.topBlock;
		IBlockState iblockstate1 = this.fillerBlock;
		int j = -1;
		int k = (int) (p_180628_6_ / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
		int l = p_180628_4_ & 15;
		int i1 = p_180628_5_ & 15;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		for (int j1 = 255; j1 >= 0; --j1) {
			if (j1 <= rand.nextInt(5)) {
				chunkPrimerIn.setBlockState(i1, j1, l, Blocks.bedrock.getDefaultState());
			} else {
				IBlockState iblockstate2 = chunkPrimerIn.getBlockState(i1, j1, l);

				if (iblockstate2.getBlock().getMaterial() == Material.air) {
					j = -1;
				} else if (iblockstate2.getBlock() == Blocks.stone) {
					if (j == -1) {
						if (k <= 0) {
							iblockstate = null;
							iblockstate1 = Blocks.stone.getDefaultState();
						} else if (j1 >= i - 4 && j1 <= i + 1) {
							iblockstate = this.topBlock;
							iblockstate1 = this.fillerBlock;
						}

						if (j1 < i && (iblockstate == null || iblockstate.getBlock().getMaterial() == Material.air)) {
							if (this.getFloatTemperature(blockpos$mutableblockpos.func_181079_c(p_180628_4_, j1, p_180628_5_)) < 0.15F) {
								iblockstate = Blocks.ice.getDefaultState();
							} else {
								iblockstate = Blocks.water.getDefaultState();
							}
						}

						j = k;

						if (j1 >= i - 1) {
							chunkPrimerIn.setBlockState(i1, j1, l, iblockstate);
						} else if (j1 < i - 7 - k) {
							iblockstate = null;
							iblockstate1 = Blocks.stone.getDefaultState();
							chunkPrimerIn.setBlockState(i1, j1, l, Blocks.gravel.getDefaultState());
						} else {
							chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
						}
					} else if (j > 0) {
						--j;
						chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);

						if (j == 0 && iblockstate1.getBlock() == Blocks.sand) {
							j = rand.nextInt(4) + Math.max(0, j1 - 63);
							iblockstate1 = iblockstate1.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? Blocks.red_sandstone.getDefaultState() : Blocks.sandstone.getDefaultState();
						}
					}
				}
			}
		}
	}

	/**
	 * Creates a mutated version of the biome and places it into the biomeList with an index equal to the original plus
	 * 128
	 */
	protected BiomeGenBase createMutation() {
		return this.createMutatedBiome(this.legacyId + 128);
	}

	protected BiomeGenBase createMutatedBiome(int p_180277_1_) {
		return new BiomeGenMutated(p_180277_1_, this);
	}

	public Class<? extends BiomeGenBase> getBiomeClass() {
		return this.getClass();
	}

	/**
	 * returns true if the biome specified is equal to this biome
	 */
	public boolean isEqualTo(BiomeGenBase biome) {
		return biome == this || biome != null && this.getBiomeClass() == biome.getBiomeClass();
	}

	public TempCategory getTempCategory() {
		return (double) this.temperature < 0.2D ? TempCategory.COLD : (double) this.temperature < 1.0D ? TempCategory.MEDIUM : TempCategory.WARM;
	}

	static {
		plains.createMutation();
		desert.createMutation();
		forest.createMutation();
		taiga.createMutation();
		swampland.createMutation();
		icePlains.createMutation();
		jungle.createMutation();
		jungleEdge.createMutation();
		coldTaiga.createMutation();
		savanna.createMutation();
		savannaPlateau.createMutation();
		mesa.createMutation();
		mesaPlateau_F.createMutation();
		mesaPlateau.createMutation();
		birchForest.createMutation();
		birchForestHills.createMutation();
		roofedForest.createMutation();
		megaTaiga.createMutation();
		extremeHills.createMutation();
		extremeHillsPlus.createMutation();
		((BiomeGenTaiga) megaTaiga).createMutatedBiome(megaTaigaHills.legacyId + 128, "Redwood Taiga Hills M");

		for (Biome biome : biomeList) {
			if (biome == null) continue;
			if (biome.getLegacyId() < 128) explorationBiomesList.add(biome);
		}

		explorationBiomesList.remove(hell);
		explorationBiomesList.remove(end);
		explorationBiomesList.remove(frozenOcean);
		explorationBiomesList.remove(extremeHillsEdge);
		temperatureNoise = new NoiseGeneratorPerlin(new Random(1234L), 1);
		GRASS_COLOR_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
		DOUBLE_PLANT_GENERATOR = new WorldGenDoublePlant();
	}

	public static class Height {

		public float rootHeight;
		public float variation;

		public Height(float rootHeightIn, float variationIn) {
			this.rootHeight = rootHeightIn;
			this.variation = variationIn;
		}

		public BiomeGenBase.Height attenuate() {
			return new BiomeGenBase.Height(this.rootHeight * 0.8F, this.variation * 0.6F);
		}

	}

}
