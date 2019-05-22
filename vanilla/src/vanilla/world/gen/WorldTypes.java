package vanilla.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.datapacks.ChunkManagerFactory;
import net.minecraft.world.datapacks.ChunkProviderFactory;
import vanilla.world.biome.BiomeGenBase;
import vanilla.world.biome.WorldChunkManager;
import vanilla.world.biome.WorldChunkManagerHell;
import vanilla.world.gen.provider.ChunkProviderFlat;
import vanilla.world.gen.provider.ChunkProviderGenerate;

public class WorldTypes {


	private static final ChunkManagerFactory
			factoryDefaultCM = (p, s, t, g) -> p == null ? new WorldChunkManager(s, t, g) : new WorldChunkManager(p.getWorld());
	private static final ChunkManagerFactory factoryFlatCM = (p, s, t, g) -> {

				FlatGeneratorInfo info = FlatGeneratorInfo.createFlatGeneratorFromString(g);
				Biome biome = Biome.getBiomeFromBiomeList(info.getBiome(), BiomeGenBase.ocean);
				return new WorldChunkManagerHell(BiomeGenBase.toGenBase(biome), 0.5F);
			};

	private static final ChunkProviderFactory
			factoryDefaultCP = p -> {
				World w = p.getWorld();
				return new ChunkProviderGenerate(w, w.getSeed(), w.getWorldInfo().isMapFeaturesEnabled(), p.getGeneratorSettings());
			};
	private static final ChunkProviderFactory factoryFlatCP = p -> {
				World w = p.getWorld();
				return new ChunkProviderFlat(w, w.getSeed(), w.getWorldInfo().isMapFeaturesEnabled(), p.getGeneratorSettings());
			};


	public static final WorldType
		DEFAULT = new WorldType("default", factoryDefaultCM, factoryDefaultCP);
	public static final WorldType FLAT = new WorldType("flat", factoryFlatCM, factoryFlatCP).weakFog();
	public static final WorldType LARGE_BIOMES = new WorldType("large_biomes", factoryDefaultCM, factoryDefaultCP);
	public static final WorldType AMPLIFIED = new WorldType("amplified", factoryDefaultCM, factoryDefaultCP);
	public static final WorldType CUSTOMIZED = new WorldType("customized", factoryDefaultCM, factoryDefaultCP);
	public static final WorldType DEFAULT_OLD = new WorldType("default_1_1", factoryDefaultCM, factoryDefaultCP).invisible();


}
