package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

import java.util.List;

public class BiomeCache {

	/**
	 * Reference to the WorldChunkManager
	 */
	private final WorldChunkManager chunkManager;

	/**
	 * The last time this BiomeCache was cleaned, in milliseconds.
	 */
	private long lastCleanupTime;
	private LongHashMap cacheMap = new LongHashMap();
	private List<Cluster> cache = Lists.newArrayList();

	public BiomeCache(WorldChunkManager chunkManagerIn) {
		this.chunkManager = chunkManagerIn;
	}

	/**
	 * Returns a biome cache block at location specified.
	 */
	public Cluster getBiomeCacheBlock(int x, int z) {
		x = x >> 4;
		z = z >> 4;
		long i = (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
		Cluster cluster = (Cluster) this.cacheMap.getValueByKey(i);

		if (cluster == null) {
			cluster = new Cluster(x, z);
			this.cacheMap.add(i, cluster);
			this.cache.add(cluster);
		}

		cluster.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
		return cluster;
	}

	public Biome getBiomeAtCoords(int x, int z, Biome onError) {
		Biome biome = this.getBiomeCacheBlock(x, z).getBiomeGenAt(x, z);
		return biome == null ? onError : biome;
	}

	/**
	 * Removes BiomeCacheBlocks from this cache that haven't been accessed in at least 30 seconds.
	 */
	public void cleanupCache() {
		long i = MinecraftServer.getCurrentTimeMillis();
		long j = i - this.lastCleanupTime;

		if (j > 7500L || j < 0L) {
			this.lastCleanupTime = i;

			for (int k = 0; k < this.cache.size(); ++k) {
				Cluster biomecache$cluster = this.cache.get(k);
				long l = i - biomecache$cluster.lastAccessTime;

				if (l > 30000L || l < 0L) {
					this.cache.remove(k--);
					long i1 = (long) biomecache$cluster.xPosition & 4294967295L | ((long) biomecache$cluster.zPosition & 4294967295L) << 32;
					this.cacheMap.remove(i1);
				}
			}
		}
	}

	/**
	 * Returns the array of cached biome types in the BiomeCacheBlock at the given location.
	 */
	public Biome[] getCachedBiomes(int x, int z) {
		return this.getBiomeCacheBlock(x, z).biomes;
	}

	public class Cluster {

		public float[] rainfallValues = new float[256];
		public Biome[] biomes = new Biome[256];
		public int xPosition;
		public int zPosition;
		public long lastAccessTime;

		public Cluster(int x, int z) {
			this.xPosition = x;
			this.zPosition = z;
			BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, x << 4, z << 4, 16, 16);
			BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, x << 4, z << 4, 16, 16, false);
		}

		public Biome getBiomeGenAt(int x, int z) {
			return this.biomes[x & 15 | (z & 15) << 4];
		}

	}

}
