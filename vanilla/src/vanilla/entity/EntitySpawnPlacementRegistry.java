package vanilla.entity;

import com.google.common.collect.Maps;
import vanilla.entity.boss.EntityDragon;
import vanilla.entity.boss.EntityWither;
import vanilla.entity.monster.*;
import vanilla.entity.passive.*;

import java.util.HashMap;

public class EntitySpawnPlacementRegistry {

	private static final HashMap<Class, SpawnPlacementType> ENTITY_PLACEMENTS = Maps.newHashMap();

	public static SpawnPlacementType getPlacementForEntity(Class entityClass) {
		return (SpawnPlacementType) ENTITY_PLACEMENTS.get(entityClass);
	}

	static {
		ENTITY_PLACEMENTS.put(EntityBat.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityChicken.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityCow.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityHorse.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityMooshroom.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityOcelot.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityPig.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityRabbit.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntitySheep.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntitySnowman.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntitySquid.class, SpawnPlacementType.IN_WATER);
		ENTITY_PLACEMENTS.put(EntityIronGolem.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityWolf.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityVillager.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityDragon.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityWither.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityBlaze.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityCaveSpider.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityCreeper.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityEnderman.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityEndermite.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityGhast.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityGiantZombie.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityGuardian.class, SpawnPlacementType.IN_WATER);
		ENTITY_PLACEMENTS.put(EntityMagmaCube.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityPigZombie.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntitySilverfish.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntitySkeleton.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntitySlime.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntitySpider.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityWitch.class, SpawnPlacementType.ON_GROUND);
		ENTITY_PLACEMENTS.put(EntityZombie.class, SpawnPlacementType.ON_GROUND);
	}
}
