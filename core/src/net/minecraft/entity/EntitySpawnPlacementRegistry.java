package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.HashMap;
import vanilla.entity.boss.EntityDragon;
import vanilla.entity.boss.EntityWither;
import vanilla.entity.monster.EntityBlaze;
import vanilla.entity.monster.EntityCaveSpider;
import vanilla.entity.monster.EntityCreeper;
import vanilla.entity.monster.EntityEnderman;
import vanilla.entity.monster.EntityEndermite;
import vanilla.entity.monster.EntityGhast;
import vanilla.entity.monster.EntityGiantZombie;
import vanilla.entity.monster.EntityGuardian;
import vanilla.entity.monster.EntityIronGolem;
import vanilla.entity.monster.EntityMagmaCube;
import vanilla.entity.monster.EntityPigZombie;
import vanilla.entity.monster.EntitySilverfish;
import vanilla.entity.monster.EntitySkeleton;
import vanilla.entity.monster.EntitySlime;
import vanilla.entity.monster.EntitySnowman;
import vanilla.entity.monster.EntitySpider;
import vanilla.entity.monster.EntityWitch;
import vanilla.entity.monster.EntityZombie;
import vanilla.entity.passive.EntityBat;
import vanilla.entity.passive.EntityChicken;
import vanilla.entity.passive.EntityCow;
import vanilla.entity.passive.EntityHorse;
import vanilla.entity.passive.EntityMooshroom;
import vanilla.entity.passive.EntityOcelot;
import vanilla.entity.passive.EntityPig;
import vanilla.entity.passive.EntityRabbit;
import vanilla.entity.passive.EntitySheep;
import vanilla.entity.passive.EntitySquid;
import vanilla.entity.passive.EntityVillager;
import vanilla.entity.passive.EntityWolf;

public class EntitySpawnPlacementRegistry
{
    private static final HashMap<Class, VanillaEntity.SpawnPlacementType> ENTITY_PLACEMENTS = Maps.newHashMap();

    public static VanillaEntity.SpawnPlacementType getPlacementForEntity(Class entityClass)
    {
        return (VanillaEntity.SpawnPlacementType)ENTITY_PLACEMENTS.get(entityClass);
    }

    static
    {
        ENTITY_PLACEMENTS.put(EntityBat.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityChicken.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityCow.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityHorse.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityMooshroom.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityOcelot.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityPig.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityRabbit.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySheep.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySnowman.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySquid.class, VanillaEntity.SpawnPlacementType.IN_WATER);
        ENTITY_PLACEMENTS.put(EntityIronGolem.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityWolf.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityVillager.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityDragon.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityWither.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityBlaze.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityCaveSpider.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityCreeper.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityEnderman.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityEndermite.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityGhast.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityGiantZombie.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityGuardian.class, VanillaEntity.SpawnPlacementType.IN_WATER);
        ENTITY_PLACEMENTS.put(EntityMagmaCube.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityPigZombie.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySilverfish.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySkeleton.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySlime.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntitySpider.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityWitch.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
        ENTITY_PLACEMENTS.put(EntityZombie.class, VanillaEntity.SpawnPlacementType.ON_GROUND);
    }
}
