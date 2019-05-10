package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BasicChunkManager implements IChunkManager {

	private final List<Biome> spawnBiomes;
	private final Biome biome;

	public BasicChunkManager(Biome biome) {
		this.biome = biome;
		this.spawnBiomes = Collections.singletonList(biome);
	}

	@Override
	public List<Biome> getBiomesToSpawnIn() {
		return spawnBiomes;
	}

	@Override
	public Biome getBiome(BlockPos pos, Biome defaultTo) {
		return biome;
	}

	@Override
	public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length) {
		if (listToReuse == null || listToReuse.length < width * length) return new float[width * length];
		return listToReuse;
	}

	@Override
	public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
		if (biomes == null || biomes.length < width * height) biomes = new Biome[width * height];
		return biomes;
	}

	@Override
	public Biome[] loadBlockGeneratorData(Biome[] oldBiomeList, int x, int z, int width, int depth) {
		if (oldBiomeList == null || oldBiomeList.length < width * depth) oldBiomeList = new Biome[width * depth];
		Arrays.fill(oldBiomeList, 0, width * depth, biome);
		return oldBiomeList;
	}

	@Override
	public Biome[] getBiomeGenAt(Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
		return this.loadBlockGeneratorData(listToReuse, x, z, width, length);
	}

	@Override
	public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
		return allowed.contains(biome);
	}

	@Override
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
		return biomes.contains(biome) ?
				new BlockPos(x - range + random.nextInt(range * 2 + 1), 0,
						z - range + random.nextInt(range * 2 + 1)) : null;
	}

	@Override
	public void cleanupCache() {

	}

}
