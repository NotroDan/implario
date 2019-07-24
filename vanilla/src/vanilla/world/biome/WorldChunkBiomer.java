package vanilla.world.biome;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.IChunkBiomer;
import vanilla.world.gen.layer.GenLayer;
import vanilla.world.gen.layer.IntCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldChunkBiomer implements IChunkBiomer {

	private GenLayer genBiomes;

	/**
	 * A GenLayer containing the indices into BiomeGenBase.biomeList[]
	 */
	private GenLayer biomeIndexLayer;

	/**
	 * The biome list.
	 */
	private BiomeCache biomeCache;
	private List<Biome> biomesToSpawnIn;
	private String field_180301_f;

	protected WorldChunkBiomer() {
		this.biomeCache = new BiomeCache(this);
		this.field_180301_f = "";
		this.biomesToSpawnIn = new ArrayList<>();
		this.biomesToSpawnIn.add(BiomeGenBase.forest);
		this.biomesToSpawnIn.add(BiomeGenBase.plains);
		this.biomesToSpawnIn.add(BiomeGenBase.taiga);
		this.biomesToSpawnIn.add(BiomeGenBase.taigaHills);
		this.biomesToSpawnIn.add(BiomeGenBase.forestHills);
		this.biomesToSpawnIn.add(BiomeGenBase.jungle);
		this.biomesToSpawnIn.add(BiomeGenBase.jungleHills);
	}

	public WorldChunkBiomer(long seed, WorldType p_i45744_3_, String p_i45744_4_) {
		this();
		this.field_180301_f = p_i45744_4_;
		GenLayer[] agenlayer = GenLayer.initializeAllBiomeGenerators(seed, p_i45744_3_, p_i45744_4_);
		this.genBiomes = agenlayer[0];
		this.biomeIndexLayer = agenlayer[1];
	}

	public WorldChunkBiomer(World worldIn) {
		this(worldIn.getSeed(), worldIn.getWorldInfo().getTerrainType(), worldIn.getWorldInfo().getGeneratorOptions());
	}

	public List<Biome> getBiomesToSpawnIn() {
		return this.biomesToSpawnIn;
	}

	/**
	 * Returns the biome generator
	 */
	public final Biome getBiome(BlockPos pos) {
		return this.getBiome(pos, null);
	}

	public Biome getBiome(BlockPos pos, Biome biomeGenBaseIn) {
		return this.biomeCache.getBiomeAtCoords(pos.getX(), pos.getZ(), biomeGenBaseIn);
	}

	/**
	 * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
	 */
	public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length) {
		IntCache.resetIntCache();

		if (listToReuse == null || listToReuse.length < width * length) {
			listToReuse = new float[width * length];
		}

		int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

		for (int i = 0; i < width * length; ++i) {
			try {
				float f = (float) Biome.getBiomeFromBiomeList(aint[i], BiomeGenBase.ocean).getIntRainfall() / 65536.0F;

				if (f > 1.0F) {
					f = 1.0F;
				}

				listToReuse[i] = f;
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("DownfallBlock");
				crashreportcategory.addCrashSection("biome id", i);
				crashreportcategory.addCrashSection("downfalls[] size", listToReuse.length);
				crashreportcategory.addCrashSection("x", x);
				crashreportcategory.addCrashSection("z", z);
				crashreportcategory.addCrashSection("w", width);
				crashreportcategory.addCrashSection("h", length);
				throw new ReportedException(crashreport);
			}
		}

		return listToReuse;
	}

	/**
	 * Returns an array of biomes for the location input.
	 */
	public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
		IntCache.resetIntCache();

		if (biomes == null || biomes.length < width * height) {
			biomes = new BiomeGenBase[width * height];
		}

		int[] aint = this.genBiomes.getInts(x, z, width, height);

		try {
			for (int i = 0; i < width * height; ++i) {
				biomes[i] = Biome.getBiomeFromBiomeList(aint[i], BiomeGenBase.ocean);
			}

			return biomes;
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
			crashreportcategory.addCrashSection("biomes[] size", biomes.length);
			crashreportcategory.addCrashSection("x", x);
			crashreportcategory.addCrashSection("z", z);
			crashreportcategory.addCrashSection("w", width);
			crashreportcategory.addCrashSection("h", height);
			throw new ReportedException(crashreport);
		}
	}

	/**
	 * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
	 * WorldChunkManager Args: oldBiomeList, x, z, width, depth
	 */
	public Biome[] loadBlockGeneratorData(Biome[] oldBiomeList, int x, int z, int width, int depth) {

		return this.getBiomeGenAt(oldBiomeList, x, z, width, depth, true);
	}

	/**
	 * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
	 * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
	 */
	public Biome[] getBiomeGenAt(Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
		IntCache.resetIntCache();

		if (listToReuse == null || listToReuse.length < width * length) {
			listToReuse = new Biome[width * length];
		}

		if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0) {
			Biome[] array = this.biomeCache.getCachedBiomes(x, z);
			System.arraycopy(array, 0, listToReuse, 0, width * length);
			return listToReuse;
		}
		int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

		for (int i = 0; i < width * length; ++i) {
			listToReuse[i] = Biome.getBiomeFromBiomeList(aint[i], BiomeGenBase.ocean);
		}

		return listToReuse;
	}

	/**
	 * checks given Chunk's Biomes against List of allowed ones
	 */
	public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
		IntCache.resetIntCache();
		int i = x - radius >> 2;
		int j = z - radius >> 2;
		int k = x + radius >> 2;
		int l = z + radius >> 2;
		int i1 = k - i + 1;
		int j1 = l - j + 1;
		int[] aint = this.genBiomes.getInts(i, j, i1, j1);

		try {
			for (int k1 = 0; k1 < i1 * j1; ++k1) {
				Biome biomegenbase = Biome.getBiome(aint[k1]);

				if (!allowed.contains(biomegenbase)) {
					return false;
				}
			}

			return true;
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
			crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
			crashreportcategory.addCrashSection("x", x);
			crashreportcategory.addCrashSection("z", z);
			crashreportcategory.addCrashSection("radius", radius);
			crashreportcategory.addCrashSection("allowed", allowed);
			throw new ReportedException(crashreport);
		}
	}

	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
		IntCache.resetIntCache();
		int i = x - range >> 2;
		int j = z - range >> 2;
		int k = x + range >> 2;
		int l = z + range >> 2;
		int i1 = k - i + 1;
		int j1 = l - j + 1;
		int[] aint = this.genBiomes.getInts(i, j, i1, j1);
		BlockPos blockpos = null;
		int k1 = 0;

		for (int l1 = 0; l1 < i1 * j1; ++l1) {
			int i2 = i + l1 % i1 << 2;
			int j2 = j + l1 / i1 << 2;
			Biome biomegenbase = Biome.getBiome(aint[l1]);

			if (biomes.contains(biomegenbase) && (blockpos == null || random.nextInt(k1 + 1) == 0)) {
				blockpos = new BlockPos(i2, 0, j2);
				++k1;
			}
		}

		return blockpos;
	}

	/**
	 * Calls the WorldChunkManager's biomeCache.cleanupCache()
	 */
	public void cleanupCache() {
		this.biomeCache.cleanupCache();
	}

}
