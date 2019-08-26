package vanilla.client;

import net.minecraft.client.MC;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.game.entity.CPlayer;
import net.minecraft.client.game.model.ModelSlime;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.ClientRegistrar;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.Player;
import net.minecraft.client.resources.ClientSideDatapack;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.logging.Log;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.*;
import net.minecraft.resources.Registrar;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import vanilla.client.audio.GuardianSound;
import vanilla.client.game.VanillaIngameModules;
import vanilla.client.game.model.*;
import vanilla.client.game.particle.VanillaParticles;
import vanilla.client.gui.block.GuiMerchant;
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
import vanilla.entity.boss.EntityDragon;
import vanilla.entity.boss.EntityWither;
import vanilla.entity.monster.*;
import vanilla.entity.passive.*;
import vanilla.item.VanillaItems;
import vanilla.tileentity.TileEntityMobSpawner;
import vanilla.world.WorldProviderEnd;
import vanilla.world.WorldProviderHell;
import vanilla.world.gen.feature.village.MerchantRecipeList;

import java.io.IOException;

public class ClientVanilla implements ClientSideDatapack {

	@Override
	public void clientInit(ClientRegistrar registrar) {
		System.out.println("Registering datapack client side");

		Registrar base = registrar.getDelegate();

		base.regInterceptor(S2DPacketOpenWindow.class, this::handleOpenWindow);
		base.regInterceptor(S1BPacketEntityAttach.class, this::handleEntityAttach);
		base.regInterceptor(S19PacketEntityStatus.class, this::handleEntityStatus);
		base.regInterceptor(S3FPacketCustomPayload.class, this::handleCustomPayload);
		base.regInterceptor(S2BPacketChangeGameState.class, this::handleGameStateChange);
		base.regInterceptor(S0EPacketSpawnObject.class, this::handleSpawnObject);



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

		RenderItem r = MC.getRenderItem();

		registrar.registerItem(VanillaItems.saddle, 0, new ModelResourceLocation("saddle"));
		r.registerItem(VanillaItems.saddle, "saddle");
		r.getItemModelMesher().registerMeshDefinition(VanillaItems.spawn_egg, stack -> new ModelResourceLocation("spawn_egg", "inventory"));
		r.registerItem(VanillaItems.carrot_on_a_stick, "carrot_on_a_stick");
		r.registerItem(VanillaItems.lead, "lead");
		r.registerItem(VanillaItems.name_tag, "name_tag");

		TileEntityRendererDispatcher.instance.register(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());


		registrar.replaceProvider(MusicTicker.MUSIC_TYPE_PROVIDER, musicTicker -> {
			Player p = MC.getPlayer();
			return p != null ? p.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER :
					p.worldObj.provider instanceof WorldProviderEnd ? BossStatus.bossName != null && BossStatus.statusBarTime > 0 ?
							MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END : p.capabilities.isCreativeMode &&
							p.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME : MusicTicker.MusicType.MENU;
		});

		new VanillaParticles().load(registrar);
		new VanillaIngameModules().load(registrar);
	}


	private boolean handleOpenWindow(S2DPacketOpenWindow p, INetHandlerPlayClient l) {
		CPlayer player = MC.getPlayer();
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
	}

	private boolean handleEntityAttach(S1BPacketEntityAttach p, INetHandlerPlayClient l) {
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
	}

	private boolean handleEntityStatus(S19PacketEntityStatus p, INetHandlerPlayClient l) {
		Entity entity = p.getEntity(((NetHandlerPlayClient) l).getClientWorldController());
		if (p.getOpCode() == 21) {
			MC.i().getSoundHandler().playSound(new GuardianSound((EntityGuardian) entity));
			return true;
		}
		return false;
	}

	private boolean handleCustomPayload(S3FPacketCustomPayload p, INetHandlerPlayClient l) {
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
	}

	private boolean handleGameStateChange(S2BPacketChangeGameState p, INetHandlerPlayClient l) {
		int i = p.getGameState();
		WorldClient w = ((NetHandlerPlayClient) l).getClientWorldController();
		Player player = MC.getPlayer();
		if (i == 10) {
			w.spawnParticle(VanillaParticles.MOB_APPEARANCE, player.posX, player.posY, player.posZ, 0.0D, 0.0D, 0.0D);
			w.playSound(player.posX, player.posY, player.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
			return true;
		}
		return false;
	}

	private boolean handleSpawnObject(S0EPacketSpawnObject p, INetHandlerPlayClient l) {
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
	}


}
