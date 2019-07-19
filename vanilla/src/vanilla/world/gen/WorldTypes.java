package vanilla.world.gen;

import net.minecraft.resources.Registrar;
import net.minecraft.resources.ServerSideLoadable;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BasicChunkBiomer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.datapacks.ChunkManagerFactory;
import net.minecraft.world.datapacks.ChunkProviderFactory;
import vanilla.world.biome.BiomeGenBase;
import vanilla.world.biome.WorldChunkBiomer;
import vanilla.world.biome.WorldChunkBiomerHell;
import vanilla.world.gen.provider.ChunkProviderFlat;
import vanilla.world.gen.provider.ChunkProviderGenerate;

public class WorldTypes implements ServerSideLoadable {


	private static final ChunkManagerFactory
			factoryDefaultCM = (p, s, t, g) -> p == null ? new WorldChunkBiomer(s, t, g) : new WorldChunkBiomer(p.getWorld());
	private static final ChunkManagerFactory factoryFlatCM = (p, s, t, g) -> {

				FlatGeneratorInfo info = FlatGeneratorInfo.createFlatGeneratorFromString(g);
				Biome biome = Biome.getBiomeFromBiomeList(info.getBiome(), BiomeGenBase.ocean);
				return new BasicChunkBiomer(BiomeGenBase.toGenBase(biome), 0.5F);
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


	public static final WorldType DEFAULT = new WorldType("default", factoryDefaultCM, factoryDefaultCP);
	public static final WorldType FLAT = new WorldType("flat", factoryFlatCM, factoryFlatCP).weakFog();
	public static final WorldType LARGE_BIOMES = new WorldType("large_biomes", factoryDefaultCM, factoryDefaultCP);
	public static final WorldType AMPLIFIED = new WorldType("amplified", factoryDefaultCM, factoryDefaultCP);
	public static final WorldType CUSTOMIZED = new WorldType("customized", factoryDefaultCM, factoryDefaultCP).disableFeatures();
	public static final WorldType DEFAULT_OLD = new WorldType("default_1_1", factoryDefaultCM, factoryDefaultCP).invisible();


	@Override
	public void load(Registrar registrar) {
		registrar.registerWorldType(DEFAULT);
		registrar.registerWorldType(FLAT);
		registrar.registerWorldType(LARGE_BIOMES);
		registrar.registerWorldType(AMPLIFIED);
		registrar.registerWorldType(CUSTOMIZED);
		registrar.registerWorldType(DEFAULT_OLD);
	}

}
