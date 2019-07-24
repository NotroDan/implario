package vanilla.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.SpawnListEntry;
import vanilla.entity.passive.EntityMooshroom;

public class BiomeGenMushroomIsland extends BiomeGenBase {

	public BiomeGenMushroomIsland(int id, String name) {
		super(id, name);
		this.theBiomeDecorator.treesPerChunk = -100;
		this.theBiomeDecorator.flowersPerChunk = -100;
		this.theBiomeDecorator.grassPerChunk = -100;
		this.theBiomeDecorator.mushroomsPerChunk = 1;
		this.theBiomeDecorator.bigMushroomsPerChunk = 1;
		this.topBlock = Blocks.mycelium.getDefaultState();
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCreatureList.add(new SpawnListEntry(EntityMooshroom.class, 8, 4, 8));
	}

}
