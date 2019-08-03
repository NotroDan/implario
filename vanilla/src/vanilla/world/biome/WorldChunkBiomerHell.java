package vanilla.world.biome;

import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Deprecated
public class WorldChunkBiomerHell extends WorldChunkBiomer {


	private final BiomeGenBase biome;
	private final float rainfall;

	public WorldChunkBiomerHell(BiomeGenBase p_i45374_1_, float p_i45374_2_) {
		this.biome = p_i45374_1_;
		this.rainfall = p_i45374_2_;
	}

	public BiomeGenBase getBiome(BlockPos pos, Biome alternative) {
		return this.biome;
	}

	/**
	 * Returns an array of biomes for the location input.
	 */
	public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
		if (biomes == null || biomes.length < width * height) {
			biomes = new BiomeGenBase[width * height];
		}

		Arrays.fill(biomes, 0, width * height, this.biome);
		return biomes;
	}

	/**
	 * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
	 */
	public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length) {
		if (listToReuse == null || listToReuse.length < width * length) {
			listToReuse = new float[width * length];
		}

		Arrays.fill(listToReuse, 0, width * length, this.rainfall);
		return listToReuse;
	}

	/**
	 * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
	 * WorldChunkManager Args: oldBiomeList, x, z, width, depth
	 */
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int depth) {
		if (oldBiomeList == null || oldBiomeList.length < width * depth) {
			oldBiomeList = new BiomeGenBase[width * depth];
		}

		Arrays.fill(oldBiomeList, 0, width * depth, this.biome);
		return oldBiomeList;
	}

	/**
	 * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
	 * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
	 */
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
		return this.loadBlockGeneratorData(listToReuse, x, z, width, length);
	}

	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
		return biomes.contains(this.biome) ? new BlockPos(x - range + random.nextInt(range * 2 + 1), 0, z - range + random.nextInt(range * 2 + 1)) : null;
	}

	/**
	 * checks given Chunk's Biomes against List of allowed ones
	 */
	public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
		return allowed.contains(this.biome);
	}

}
