package vanilla;

import net.minecraft.client.MC;
import net.minecraft.client.game.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.Lang;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.logging.Log;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.*;
import net.minecraft.resources.ServerSideLoadable;
import net.minecraft.resources.Registrar;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import vanilla.client.audio.GuardianSound;
import vanilla.client.game.particle.VanillaParticles;
import vanilla.client.gui.block.GuiMerchant;
import vanilla.client.gui.block.HorseInv;
import vanilla.entity.EntityLeashKnot;
import vanilla.entity.IMerchant;
import vanilla.entity.NpcMerchant;
import vanilla.entity.VanillaEntity;
import vanilla.entity.monster.EntityGuardian;
import vanilla.entity.passive.EntityHorse;
import vanilla.inventory.ContainerMerchant;
import vanilla.world.gen.feature.village.MerchantRecipeList;

import java.io.IOException;

public class VPackets implements ServerSideLoadable {

	@Override
	public void load(Registrar registrar) {

		// Server packets
		registrar.regInterceptor(S2DPacketOpenWindow.class,      this::handleOpenWindow);
		registrar.regInterceptor(S1BPacketEntityAttach.class,    this::handleEntityAttach);
		registrar.regInterceptor(S19PacketEntityStatus.class,    this::handleEntityStatus);
		registrar.regInterceptor(S3FPacketCustomPayload.class,   this::handleCustomPayload);
		registrar.regInterceptor(S2BPacketChangeGameState.class, this::handleGameStateChange);
		registrar.regInterceptor(S0EPacketSpawnObject.class,     this::handleSpawnObject);

		// Client packets
		registrar.regInterceptor(C17PacketCustomPayload.class,   this::handleCustomPayload);

	}

	private boolean handleOpenWindow(S2DPacketOpenWindow p, INetHandlerPlayClient l) {
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

	private boolean handleCustomPayload(C17PacketCustomPayload p, INetHandlerPlayServer l) {
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
		return false;
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
		EntityPlayer player = MC.getPlayer();
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
