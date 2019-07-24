package vanilla.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.SpawnListEntry;
import vanilla.entity.monster.EntityEnderman;

public class BiomeGenEnd extends BiomeGenBase {

	public BiomeGenEnd(int id, String name) {
		super(id, name);
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 10, 4, 4));
		this.topBlock = Blocks.dirt.getDefaultState();
		this.fillerBlock = Blocks.dirt.getDefaultState();
		this.theBiomeDecorator = new BiomeEndDecorator();
	}

	/**
	 * takes temperature, returns color
	 */
	public int getSkyColorByTemp(float temp) {
		return 0;
	}

}
