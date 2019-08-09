package vanilla;

import net.minecraft.client.MC;
import net.minecraft.client.game.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.ClientRegistrar;
import net.minecraft.client.resources.ClientSideLoadable;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.resources.ServerSideLoadable;
import net.minecraft.resources.Registrar;
import vanilla.client.game.model.*;
import vanilla.client.renderer.entity.RenderLeashKnot;
import vanilla.client.renderer.entity.RenderMinecartMobSpawner;
import vanilla.client.renderer.entity.vanilla.*;
import vanilla.entity.EntityLeashKnot;
import vanilla.entity.VanillaEntity;
import vanilla.entity.ai.EntityMinecartMobSpawner;
import vanilla.entity.boss.EntityDragon;
import vanilla.entity.boss.EntityWither;
import vanilla.entity.monster.*;
import vanilla.entity.passive.*;

public class VEntities implements ServerSideLoadable, ClientSideLoadable {


	@Override
	public void load(Registrar registrar) {

		registrar.registerEntity(EntityLeashKnot.class, "LeashKnot", 8);
		registrar.registerEntity(EntityMinecartMobSpawner.class, EntityMinecart.EnumMinecartType.SPAWNER.getName(), 47);
		registrar.registerEntity(VanillaEntity.class, "Mob", 48);
		registrar.registerEntity(EntityMob.class, "Monster", 49);
		registrar.registerMob(EntityCreeper.class, "Creeper", 50, 894731, 0);
		registrar.registerMob(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
		registrar.registerMob(EntitySpider.class, "Spider", 52, 3419431, 11013646);
		registrar.registerEntity(EntityGiantZombie.class, "Giant", 53);
		registrar.registerMob(EntityZombie.class, "Zombie", 54, 44975, 7969893);
		registrar.registerMob(EntitySlime.class, "Slime", 55, 5349438, 8306542);
		registrar.registerMob(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
		registrar.registerMob(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
		registrar.registerMob(EntityEnderman.class, "Enderman", 58, 1447446, 0);
		registrar.registerMob(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
		registrar.registerMob(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
		registrar.registerMob(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
		registrar.registerMob(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
		registrar.registerEntity(EntityDragon.class, "EnderDragon", 63);
		registrar.registerEntity(EntityWither.class, "WitherBoss", 64);
		registrar.registerMob(EntityBat.class, "Bat", 65, 4996656, 986895);
		registrar.registerMob(EntityWitch.class, "Witch", 66, 3407872, 5349438);
		registrar.registerMob(EntityEndermite.class, "Endermite", 67, 1447446, 7237230);
		registrar.registerMob(EntityGuardian.class, "Guardian", 68, 5931634, 15826224);
		registrar.registerMob(EntityPig.class, "Pig", 90, 15771042, 14377823);
		registrar.registerMob(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
		registrar.registerMob(EntityCow.class, "Cow", 92, 4470310, 10592673);
		registrar.registerMob(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
		registrar.registerMob(EntitySquid.class, "Squid", 94, 2243405, 7375001);
		registrar.registerMob(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
		registrar.registerMob(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
		registrar.registerEntity(EntitySnowman.class, "SnowMan", 97);
		registrar.registerMob(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
		registrar.registerEntity(EntityIronGolem.class, "VillagerGolem", 99);
		registrar.registerMob(EntityHorse.class, "EntityHorse", 100, 12623485, 15656192);
		registrar.registerMob(EntityRabbit.class, "Rabbit", 101, 10051392, 7555121);
		registrar.registerMob(EntityVillager.class, "Villager", 120, 5651507, 12422002);

	}

	@Override
	public void load(ClientRegistrar registrar) {

		RenderManager m = MC.i().getRenderManager();

		registrar.registerEntity(EntityCaveSpider.class, new RenderCaveSpider(m));
		registrar.registerEntity(EntitySpider.class, new RenderSpider<>(m));
		registrar.registerEntity(EntityPig.class, new RenderPig(m, new ModelPig(), 0.7F));
		registrar.registerEntity(EntitySheep.class, new RenderSheep(m, new ModelSheep2(), 0.7F));
		registrar.registerEntity(EntityCow.class, new RenderCow(m, new ModelCow(), 0.7F));
		registrar.registerEntity(EntityMooshroom.class, new RenderMooshroom(m, new ModelCow(), 0.7F));
		registrar.registerEntity(EntityWolf.class, new RenderWolf(m, new ModelWolf(), 0.5F));
		registrar.registerEntity(EntityChicken.class, new RenderChicken(m, new ModelChicken(), 0.3F));
		registrar.registerEntity(EntityOcelot.class, new RenderOcelot(m, new ModelOcelot(), 0.4F));
		registrar.registerEntity(EntityRabbit.class, new RenderRabbit(m, new ModelRabbit(), 0.3F));
		registrar.registerEntity(EntitySilverfish.class, new RenderSilverfish(m));
		registrar.registerEntity(EntityEndermite.class, new RenderEndermite(m));
		registrar.registerEntity(EntityCreeper.class, new RenderCreeper(m));
		registrar.registerEntity(EntityEnderman.class, new RenderEnderman(m));
		registrar.registerEntity(EntitySnowman.class, new RenderSnowMan(m));
		registrar.registerEntity(EntitySkeleton.class, new RenderSkeleton(m));
		registrar.registerEntity(EntityWitch.class, new RenderWitch(m));
		registrar.registerEntity(EntityBlaze.class, new RenderBlaze(m));
		registrar.registerEntity(EntityPigZombie.class, new RenderPigZombie(m));
		registrar.registerEntity(EntityZombie.class, new RenderZombie(m));
		registrar.registerEntity(EntitySlime.class, new RenderSlime(m, new ModelSlime(16), 0.25F));
		registrar.registerEntity(EntityMagmaCube.class, new RenderMagmaCube(m));
		registrar.registerEntity(EntityGiantZombie.class, new RenderGiantZombie(m, new ModelZombie(), 0.5F, 6.0F));
		registrar.registerEntity(EntityGhast.class, new RenderGhast(m));
		registrar.registerEntity(EntitySquid.class, new RenderSquid(m, new ModelSquid(), 0.7F));
		registrar.registerEntity(EntityVillager.class, new RenderVillager(m));
		registrar.registerEntity(EntityIronGolem.class, new RenderIronGolem(m));
		registrar.registerEntity(EntityBat.class, new RenderBat(m));
		registrar.registerEntity(EntityGuardian.class, new RenderGuardian(m));
		registrar.registerEntity(EntityDragon.class, new RenderDragon(m));
		registrar.registerEntity(EntityWither.class, new RenderWither(m));
		registrar.registerEntity(EntityLeashKnot.class, new RenderLeashKnot(m));
		registrar.registerEntity(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(m));
		registrar.registerEntity(EntityHorse.class, new RenderHorse(m, new ModelHorse(), 0.75F));
	}

}
