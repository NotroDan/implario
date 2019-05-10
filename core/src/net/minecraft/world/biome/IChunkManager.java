package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;

import java.util.List;
import java.util.Random;

public interface IChunkManager {

	List<Biome> getBiomesToSpawnIn();

	default Biome getBiome(BlockPos pos) {
		return getBiome(pos, Biome.VOID);
	}

	Biome getBiome(BlockPos pos, Biome defaultTo);

	float[] getRainfall(float[] listToReuse, int x, int z, int width, int length);

	Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height);

	Biome[] loadBlockGeneratorData(Biome[] oldBiomeList, int x, int z, int width, int depth);

	Biome[] getBiomeGenAt(Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag);

	boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed);

	BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random);

	void cleanupCache();

}
