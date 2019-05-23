package vanilla;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.FenceClickedEvent;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.MC;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.game.entity.EntityPlayerSP;
import net.minecraft.client.game.model.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.Lang;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.logging.Log;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.*;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Domain;
import net.minecraft.resources.event.events.*;
import net.minecraft.resources.event.events.block.BlockDropEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vanilla.block.BlockMobSpawner;
import vanilla.block.VBlockMushroom;
import vanilla.block.VBlockSapling;
import vanilla.client.audio.GuardianSound;
import vanilla.client.gui.block.GuiMerchant;
import vanilla.client.gui.block.GuiScreenHorseInventory;
import vanilla.client.gui.block.HorseInv;
import vanilla.client.renderer.entity.RenderLeashKnot;
import vanilla.client.renderer.entity.RenderMinecartMobSpawner;
import vanilla.client.renderer.entity.vanilla.*;
import vanilla.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import vanilla.entity.EntityLeashKnot;
import vanilla.entity.IMerchant;
import vanilla.entity.NpcMerchant;
import vanilla.entity.VanillaEntity;
import vanilla.entity.ai.EntityMinecartMobSpawner;
import vanilla.entity.boss.BossStatus;
import vanilla.entity.boss.DragonPartRedirecter;
import vanilla.entity.boss.EntityDragon;
import vanilla.entity.boss.EntityWither;
import vanilla.entity.monster.*;
import vanilla.entity.passive.*;
import vanilla.inventory.ContainerMerchant;
import vanilla.item.*;
import vanilla.tileentity.TileEntityMobSpawner;
import vanilla.world.SleepChecker;
import vanilla.world.WorldProviderEnd;
import vanilla.world.WorldProviderHell;
import vanilla.world.gen.feature.village.MerchantRecipeList;

import java.io.IOException;

import static net.minecraft.block.Block.*;
import static net.minecraft.entity.EntityList.addMapping;
import static net.minecraft.inventory.creativetab.CreativeTabs.tabRedstone;

public class Vanilla extends Datapack {


	public static final Domain VANILLA = new Domain("vanilla");

	public Vanilla() {
		super(VANILLA);
	}

	@Override
	public void preinit() {

		registrar.registerItem(329, "saddle", new ItemSaddle().setUnlocalizedName("saddle"));
		registrar.registerItem(383, "spawn_egg", new ItemMonsterPlacer().setUnlocalizedName("monsterPlacer"));
		registrar.registerItem(398, "carrot_on_a_stick", new ItemCarrotOnAStick().setUnlocalizedName("carrotOnAStick"));
		registrar.registerItem(420, "lead", new ItemLead().setUnlocalizedName("leash"));
		registrar.registerItem(421, "name_tag", new ItemNameTag().setUnlocalizedName("nameTag"));

		registrar.registerBlock(52, "mob_spawner", new BlockMobSpawner().setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("mobSpawner").disableStats().setCreativeTab(tabRedstone));

		registrar.overrideItem(351, "dye", new VItemDye().setUnlocalizedName("dyePowder"));
		registrar.overrideBlock(6, "sapling", new VBlockSapling().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("sapling"));



		Block redMushroom = new VBlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setLightLevel(0.125F).setUnlocalizedName("mushroom");
		registrar.overrideBlock(39, "brown_mushroom", redMushroom);
		Block brownBushroom = new VBlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("mushroom");
		registrar.overrideBlock(40, "red_mushroom", brownBushroom);
		registrar.overrideBlock(99, "brown_mushroom_block", new BlockHugeMushroom(Material.wood, MapColor.dirtColor, redMushroom).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom"));
		registrar.overrideBlock(100, "red_mushroom_block", new BlockHugeMushroom(Material.wood, MapColor.redColor, brownBushroom).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom"));

		//Todo wrap with registrar
		TileEntity.register(TileEntityMobSpawner.class, "MobSpawner");
		addMapping(EntityLeashKnot.class, "LeashKnot", 8);
		addMapping(EntityMinecartMobSpawner.class, EntityMinecart.EnumMinecartType.SPAWNER.getName(), 47);
		addMapping(VanillaEntity.class, "Mob", 48);
		addMapping(EntityMob.class, "Monster", 49);
		addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
		addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
		addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
		addMapping(EntityGiantZombie.class, "Giant", 53);
		addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
		addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
		addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
		addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
		addMapping(EntityEnderman.class, "Enderman", 58, 1447446, 0);
		addMapping(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
		addMapping(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
		addMapping(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
		addMapping(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
		addMapping(EntityDragon.class, "EnderDragon", 63);
		addMapping(EntityWither.class, "WitherBoss", 64);
		addMapping(EntityBat.class, "Bat", 65, 4996656, 986895);
		addMapping(EntityWitch.class, "Witch", 66, 3407872, 5349438);
		addMapping(EntityEndermite.class, "Endermite", 67, 1447446, 7237230);
		addMapping(EntityGuardian.class, "Guardian", 68, 5931634, 15826224);
		addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
		addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
		addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
		addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
		addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
		addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
		addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
		addMapping(EntitySnowman.class, "SnowMan", 97);
		addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
		addMapping(EntityIronGolem.class, "VillagerGolem", 99);
		addMapping(EntityHorse.class, "EntityHorse", 100, 12623485, 15656192);
		addMapping(EntityRabbit.class, "Rabbit", 101, 10051392, 7555121);
		addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);

	}

	@Override
	public void init() {

		registrar.regListener(DamageByEntityEvent.class, new DragonPartRedirecter());
		registrar.regListener(TrySleepEvent.class, new SleepChecker());
		registrar.regListener(PlayerEntityActionEvent.class, e -> {
			if (e.getAction() == C0BPacketEntityAction.Action.OPEN_INVENTORY)
				if (e.getPlayer().ridingEntity instanceof EntityHorse)
					((EntityHorse) e.getPlayer().ridingEntity).openGUI(e.getPlayer());
			if (e.getAction() == C0BPacketEntityAction.Action.RIDING_JUMP)
				if (e.getPlayer().ridingEntity instanceof EntityHorse)
					((EntityHorse) e.getPlayer().ridingEntity).setJumpPower(e.getAux());
		});

		registrar.regListener(UpdateEntityToSpectatorEvent.class, e -> {
			Entity entity = e.getTrackerEntry().trackedEntity;
			if (!(entity instanceof VanillaEntity)) return;
			VanillaEntity ve = (VanillaEntity) entity;
			Entity leashed = ve.getLeashedToEntity();
			if (leashed != null) {
				e.getPlayer().playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(1, entity, leashed));
			}
		});


		registrar.regListener(ProjectileHitEvent.class, e -> {

			EntityThrowable t = e.getThrowable();

			if (t instanceof EntityEgg) {
				if (t.worldObj.isClientSide || t.rand.nextInt(8) != 0) return;
				int i = 1;

				if (t.rand.nextInt(32) == 0) i = 4;

				for (int j = 0; j < i; ++j) {
					EntityChicken entitychicken = new EntityChicken(t.worldObj);
					entitychicken.setGrowingAge(-24000);
					entitychicken.setLocationAndAngles(t.posX, t.posY, t.posZ, t.rotationYaw, 0.0F);
					t.worldObj.spawnEntityInWorld(entitychicken);
				}
			} else if (t instanceof EntitySnowball) {
				if (e.getObject().entityHit != null) {
					int i = 0;
					if (e.getObject().entityHit instanceof EntityBlaze) i = 3;

					e.getObject().entityHit.attackEntityFrom(DamageSource.causeThrownDamage(e.getThrowable(), e.getThrowable().getThrower()), (float) i);
				}
			}

		});

		registrar.regListener(PlayerEnderPearlEvent.class, e -> {
			EntityEnderPearl p = e.getPearl();
			EntityPlayerMP m = e.getPlayer();
			if (p.rand.nextFloat() < 0.05F && p.worldObj.getGameRules().getBoolean("doMobSpawning")) {
				EntityEndermite entityendermite = new EntityEndermite(p.worldObj);
				entityendermite.setSpawnedByPlayer(true);
				entityendermite.setLocationAndAngles(m.posX, m.posY, m.posZ, m.rotationYaw, m.rotationPitch);
				p.worldObj.spawnEntityInWorld(entityendermite);
			}

		});

		registrar.regInterceptor(S2DPacketOpenWindow.class, (p, l) -> {
			EntityPlayerSP player = MC.getPlayer();
			if ("minecraft:villager".equals(p.getGuiId())) {
				player.openGui(IMerchant.class, new NpcMerchant(player, p.getWindowTitle()));
				player.openContainer.windowId = p.getWindowId();
				return true;
			}
			if ("EntityHorse".equals(p.getGuiId())) {
				Entity entity = ((NetHandlerPlayClient) l).getClientWorldController().getEntityByID(p.getEntityId());

				if (entity instanceof EntityHorse) {
					HorseInv horseInv = new HorseInv((EntityHorse) entity, new AnimalChest(p.getWindowTitle(), p.getSlotCount()));
					player.openGui(HorseInv.class, horseInv);
					player.openContainer.windowId = p.getWindowId();
				}
				return true;
			}
			return false;
		});

		registrar.regInterceptor(S1BPacketEntityAttach.class, (p, l) -> {
			WorldClient cl = ((NetHandlerPlayClient) l).getClientWorldController();
			Entity entity = cl.getEntityByID(p.getEntityId());
			Entity entity1 = cl.getEntityByID(p.getVehicleEntityId());

			if (p.getLeash() == 0) {
				boolean flag = false;

				if (p.getEntityId() == MC.getPlayer().getEntityId()) {
					entity = MC.getPlayer();

					if (entity1 instanceof EntityBoat)
						((EntityBoat) entity1).setIsBoatEmpty(false);

					flag = entity.ridingEntity == null && entity1 != null;
				} else if (entity1 instanceof EntityBoat)
					((EntityBoat) entity1).setIsBoatEmpty(true);

				if (entity == null)
					return true;

				entity.mountEntity(entity1);

				if (flag) MC.i().ingameGUI.setRecordPlaying(Lang.format("mount.onboard",
						"SHIFT"), false);
			} else if (p.getLeash() == 1 && entity instanceof VanillaEntity) {
				if (entity1 != null)
					((VanillaEntity) entity).setLeashedToEntity(entity1, false);
				else
					((VanillaEntity) entity).clearLeashed(false, false);
			}
			return true;
		});

		registrar.regInterceptor(C17PacketCustomPayload.class, (p, l) -> {

			if ("MC|TrSel".equals(p.getChannelName())) {
				try {
					int i = p.getBufferData().readInt();
					Container container = l.getPlayer().openContainer;

					if (container instanceof ContainerMerchant) {
						((ContainerMerchant) container).setCurrentRecipeIndex(i);
					}
				} catch (Exception e) {
					Log.MAIN.error("Couldn\'t select trade");
					Log.MAIN.exception(e);
				}
				return true;
			}
			return false; // ToDo: Обработка ретурнеда
		});

		registrar.regInterceptor(S19PacketEntityStatus.class, (p, l) -> {
			Entity entity = p.getEntity(((NetHandlerPlayClient) l).getClientWorldController());
			if (p.getOpCode() == 21) {
				MC.i().getSoundHandler().playSound(new GuardianSound((EntityGuardian) entity));
				return true;
			}
			return false;
		});

		registrar.regInterceptor(S3FPacketCustomPayload.class, (p, l) -> {

			if ("MC|TrList".equals(p.getChannelName())) {
				PacketBuffer packetbuffer = p.getBufferData();

				try {
					int i = packetbuffer.readInt();
					GuiScreen guiscreen = MC.i().currentScreen;

					if (guiscreen instanceof GuiMerchant && i == MC.getPlayer().openContainer.windowId) {
						IMerchant imerchant = ((GuiMerchant) guiscreen).getMerchant();
						MerchantRecipeList merchantrecipelist = MerchantRecipeList.readFromBuf(packetbuffer);
						imerchant.setRecipes(merchantrecipelist);
					}
				} catch (IOException ioexception) {
					Log.MAIN.error("Couldn\'t load trade info");
					Log.MAIN.exception(ioexception);
				} finally {
					packetbuffer.release();
				}
				return true;
			}
			return false;
		});

		registrar.regInterceptor(S0EPacketSpawnObject.class, (p, l) -> {
			if (p.getType() == 77) {

				double d0 = (double) p.getX() / 32.0D;
				double d1 = (double) p.getY() / 32.0D;
				double d2 = (double) p.getZ() / 32.0D;
				NetHandlerPlayClient handler = (NetHandlerPlayClient) l;
				WorldClient w = handler.getClientWorldController();
				Entity entity = new EntityLeashKnot(w, new BlockPos(MathHelper.floor_double(d0), MathHelper.floor_double(d1), MathHelper.floor_double(d2)));
				p.setXi(0);
				handler.addEntityToWorld(p, entity);

				return true;
			}
			return false;
		});

		registrar.regListener(FenceClickedEvent.class, e ->
				e.returnValue = ItemLead.attachToFence(e.getPlayer(), e.getWorld(), e.getPos()));

		registrar.regListener(BlockDropEvent.class, e -> {
			World w = e.getWorld();
			BlockPos pos = e.getPosition();
			if (e.getBlock().getBlock() == Blocks.monster_egg) {
				e.cancelDefaultDrop();
				if (!w.isClientSide && w.getGameRules().getBoolean("doTileDrops")) {
					EntitySilverfish entitysilverfish = new EntitySilverfish(w);
					entitysilverfish.setLocationAndAngles((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
					w.spawnEntityInWorld(entitysilverfish);
					entitysilverfish.spawnExplosionParticle();
				}
			}
		});


		registerGuis();

		registerDispenserBehaviours();
	}

	@Override
	public void postinit() {

		RenderManager m = MC.i().getRenderManager();

		m.regMapping(EntityCaveSpider.class, new RenderCaveSpider(m));
		m.regMapping(EntitySpider.class, new RenderSpider(m));
		m.regMapping(EntityPig.class, new RenderPig(m, new ModelPig(), 0.7F));
		m.regMapping(EntitySheep.class, new RenderSheep(m, new ModelSheep2(), 0.7F));
		m.regMapping(EntityCow.class, new RenderCow(m, new ModelCow(), 0.7F));
		m.regMapping(EntityMooshroom.class, new RenderMooshroom(m, new ModelCow(), 0.7F));
		m.regMapping(EntityWolf.class, new RenderWolf(m, new ModelWolf(), 0.5F));
		m.regMapping(EntityChicken.class, new RenderChicken(m, new ModelChicken(), 0.3F));
		m.regMapping(EntityOcelot.class, new RenderOcelot(m, new ModelOcelot(), 0.4F));
		m.regMapping(EntityRabbit.class, new RenderRabbit(m, new ModelRabbit(), 0.3F));
		m.regMapping(EntitySilverfish.class, new RenderSilverfish(m));
		m.regMapping(EntityEndermite.class, new RenderEndermite(m));
		m.regMapping(EntityCreeper.class, new RenderCreeper(m));
		m.regMapping(EntityEnderman.class, new RenderEnderman(m));
		m.regMapping(EntitySnowman.class, new RenderSnowMan(m));
		m.regMapping(EntitySkeleton.class, new RenderSkeleton(m));
		m.regMapping(EntityWitch.class, new RenderWitch(m));
		m.regMapping(EntityBlaze.class, new RenderBlaze(m));
		m.regMapping(EntityPigZombie.class, new RenderPigZombie(m));
		m.regMapping(EntityZombie.class, new RenderZombie(m));
		m.regMapping(EntitySlime.class, new RenderSlime(m, new ModelSlime(16), 0.25F));
		m.regMapping(EntityMagmaCube.class, new RenderMagmaCube(m));
		m.regMapping(EntityGiantZombie.class, new RenderGiantZombie(m, new ModelZombie(), 0.5F, 6.0F));
		m.regMapping(EntityGhast.class, new RenderGhast(m));
		m.regMapping(EntitySquid.class, new RenderSquid(m, new ModelSquid(), 0.7F));
		m.regMapping(EntityVillager.class, new RenderVillager(m));
		m.regMapping(EntityIronGolem.class, new RenderIronGolem(m));
		m.regMapping(EntityBat.class, new RenderBat(m));
		m.regMapping(EntityGuardian.class, new RenderGuardian(m));
		m.regMapping(EntityDragon.class, new RenderDragon(m));
		m.regMapping(EntityWither.class, new RenderWither(m));
		m.regMapping(EntityLeashKnot.class, new RenderLeashKnot(m));
		m.regMapping(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(m));
		m.regMapping(EntityHorse.class, new RenderHorse(m, new ModelHorse(), 0.75F));

		TileEntityRendererDispatcher.instance.register(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());

		MC.i().getMusicTicker().musicTypeSupplier = () -> {
			EntityPlayer p = MC.getPlayer();
			return p != null ? p.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER :
					p.worldObj.provider instanceof WorldProviderEnd ? BossStatus.bossName != null && BossStatus.statusBarTime > 0 ?
							MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END : p.capabilities.isCreativeMode &&
							p.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME : MusicTicker.MusicType.MENU;
		};

	}

	private void registerGuis() {
		registrar.regGui(IMerchant.class, (p, merchant, serverSide) -> {
			if (!serverSide) {
				MC.displayGuiScreen(new GuiMerchant(p.inventory, merchant, p.worldObj));
			}
		});
		registrar.regGui(HorseInv.class, (p, horseinv, serverSide) -> {
			if (!serverSide) {
				MC.displayGuiScreen(new GuiScreenHorseInventory(p.inventory, horseinv.inv, horseinv.horse));
			}
		});
	}

	private void registerDispenserBehaviours() {
		Dispensers.init();
	}

	@Override
	protected void unload() {

	}

}
