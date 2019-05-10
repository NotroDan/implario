package vanilla.world.biome;

import vanilla.entity.monster.EntityGhast;
import vanilla.entity.monster.EntityMagmaCube;
import vanilla.entity.monster.EntityPigZombie;
import net.minecraft.world.biome.SpawnListEntry;

public class BiomeGenHell extends BiomeGenBase {

	public BiomeGenHell(int id, String name) {
		super(id, name);
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 50, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 100, 4, 4));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityMagmaCube.class, 1, 4, 4));
	}

}
