package vanilla.world.biome;

import net.minecraft.world.World;
import net.minecraft.world.biome.TempCategory;
import net.minecraft.world.chunk.anvil.AnvilChunkPrimer;

import java.util.Random;

public class BiomeGenOcean extends BiomeGenBase {

	public BiomeGenOcean(int id, String name) {
		super(id, name);
		this.spawnableCreatureList.clear();
	}

	public TempCategory getTempCategory() {
		return TempCategory.OCEAN;
	}

	public void genTerrainBlocks(World worldIn, Random rand, AnvilChunkPrimer chunkPrimerIn, int p_180622_4_, int p_180622_5_, double p_180622_6_) {
		super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, p_180622_4_, p_180622_5_, p_180622_6_);
	}

}
