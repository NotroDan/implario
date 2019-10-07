package vanilla;

import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.Server;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.resources.Registrar;
import net.minecraft.resources.ServerSideLoadable;
import net.minecraft.resources.event.ServerEvents;
import net.minecraft.resources.event.events.*;
import net.minecraft.resources.event.events.block.BlockDropEvent;
import net.minecraft.resources.event.events.player.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.Sys;
import vanilla.entity.VanillaEntity;
import vanilla.entity.monster.EntityBlaze;
import vanilla.entity.monster.EntityEndermite;
import vanilla.entity.monster.EntitySilverfish;
import vanilla.entity.passive.EntityChicken;
import vanilla.entity.passive.EntityHorse;
import vanilla.entity.passive.EntityPig;
import vanilla.entity.passive.EntityRabbit;
import vanilla.item.ItemLead;
import vanilla.world.SleepChecker;

import java.util.Random;

import static net.minecraft.resources.Datapack.isServerSide;
import static vanilla.Vanilla.VANILLA;

public class VEvents implements ServerSideLoadable {

	@Override
	public void load(Registrar registrar) {
		registrar.registerListener(ServerEvents.playerMove, this::handlePlayerMove, -5);
		registrar.registerListener(ServerEvents.playerMountMove, this::handleMountMove, -5, true);
		registrar.registerListener(ServerEvents.playerTick, this::handlePlayerTick, -5);
		registrar.registerListener(ServerEvents.playerFall, this::handlePlayerFall, -5, true);
		registrar.registerListener(ServerEvents.playerDisconnect, e -> e.getPlayer().triggerAchievement(StatList.leaveGameStat), -5);
		registrar.registerListener(ServerEvents.playerJump, e -> e.getPlayer().triggerAchievement(StatList.jumpStat), -5);
		registrar.registerListener(ServerEvents.playerItemDrop, this::handleItemDrop, -5, true);
		registrar.registerListener(ServerEvents.playerDeath, e -> e.getPlayer().triggerAchievement(StatList.deathsStat), -5);
		registrar.registerListener(ServerEvents.playerSleep, new SleepChecker());
		registrar.registerListener(ServerEvents.playerAction, this::handleEntityAction, -5);
		registrar.registerListener(ServerEvents.trackerUpdate, this::handlerTrackerUpdate, -5);
		registrar.registerListener(ServerEvents.projectileHit, this::handleProjectileHit, -5);
		registrar.registerListener(ServerEvents.playerTeleportPearl, this::handlePlayerEnderPearl, -5, true);
		registrar.registerListener(ServerEvents.blockDrop, this::handleBlockDrop, -5, true);
		registrar.registerListener(ServerEvents.playerInteract, this::handleInteract, -5, true);
	}

	private void handleItemDrop(PlayerItemDropEvent e) {
		if (e.isTraceItem()) e.getPlayer().triggerAchievement(StatList.dropStat);
	}

	private void handlePlayerFall(PlayerFallEvent e) {
		Player p = e.getPlayer();
		float distance = e.getDistance();
		if (distance >= 2.0F) p.addStat(StatList.distanceFallenStat, (int) Math.round((double) distance * 100.0D));
	}

	private void handlePlayerTick(PlayerTickEvent e) {
		if (e.getPlayer().worldObj.isClientSide) return;
		e.getPlayer().triggerAchievement(StatList.minutesPlayedStat);
		if (e.getPlayer().isEntityAlive()) e.getPlayer().triggerAchievement(StatList.timeSinceDeathStat);
	}

	private void handleMountMove(PlayerMountMoveEvent e) {
		Player player = e.getPlayer();
		Entity entity = player.ridingEntity;
		if (entity == null) return;

		double dx = e.getDstX() - e.getSrcX();
		double dy = e.getDstY() - e.getSrcY();
		double dz = e.getDstZ() - e.getSrcZ();

		int i = Math.round(MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz) * 100.0F);

		if (i <= 0) return;
		if (entity instanceof EntityMinecart) {
			player.addStat(StatList.distanceByMinecartStat, i);

			if (player.startMinecartRidingCoordinate == null) {
				player.startMinecartRidingCoordinate = new BlockPos(player);
			} else if (player.startMinecartRidingCoordinate.distanceSq(
					MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ)
																	  ) >= 1000000.0D) {
				player.triggerAchievement(AchievementList.onARail);
			}
		} else if (entity instanceof EntityBoat) {
			player.addStat(StatList.distanceByBoatStat, i);
		} else if (entity instanceof EntityPig) {
			player.addStat(StatList.distanceByPigStat, i);
		} else if (entity instanceof EntityHorse) {
			player.addStat(StatList.distanceByHorseStat, i);
		}
	}

	private void handlePlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();

		if (p.ridingEntity != null) return;

		double x = event.getDstX() - event.getSrcX();
		double y = event.getDstY() - event.getSrcY();
		double z = event.getDstZ() - event.getSrcZ();

		if (p.isInsideOfMaterial(Material.water)) {
			int distance = Math.round(MathHelper.sqrt_double(x * x + y * y + z * z) * 100.0F);
			if (distance <= 0) return;

			p.addStat(StatList.distanceDoveStat, distance);
			p.addExhaustion(0.015F * (float) distance * 0.01F);

		} else if (p.isInWater()) {
			int distance = Math.round(MathHelper.sqrt_double(x * x + z * z) * 100.0F);

			if (distance <= 0) return;

			p.addStat(StatList.distanceSwumStat, distance);
			p.addExhaustion(0.015F * (float) distance * 0.01F);

		} else if (p.isOnLadder()) {

			if (y > 0.0D) p.addStat(StatList.distanceClimbedStat, (int) Math.round(y * 100.0D));

		} else if (p.onGround) {

			int distance = Math.round(MathHelper.sqrt_double(x * x + z * z) * 100.0F);
			if (distance <= 0) return;

			p.addStat(StatList.distanceWalkedStat, distance);

			if (p.isSprinting()) {
				p.addStat(StatList.distanceSprintedStat, distance);
				p.addExhaustion(0.1F * (float) distance * 0.01F);
			} else {
				if (p.isSneaking()) p.addStat(StatList.distanceCrouchedStat, distance);

				p.addExhaustion(0.01F * (float) distance * 0.01F);
			}
		} else {
			int l = Math.round(MathHelper.sqrt_double(x * x + z * z) * 100.0F);
			if (l > 25) p.addStat(StatList.distanceFlownStat, l);
		}

	}

	private void handleInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getHeldItem();
		if (item != null && p.isSneaking()) return;
		if (p.getEntityWorld().isClientSide) return;
		if (e.getBlockState().getBlock() instanceof BlockFence) {
			ItemLead.attachToFence(e.getPlayer(), e.getPlayer().getEntityWorld(), e.getPos());
			e.cancel(true);
		}
	}

	private void handleBlockDrop(BlockDropEvent e) {
		World w = e.getWorld();
		BlockPos pos = e.getPosition();
		if (e.getBlock().getBlock() != Blocks.monster_egg) return;
		e.cancelDefaultDrop();
		if (!w.isClientSide && w.getGameRules().getBoolean("doTileDrops")) {
			EntitySilverfish entitysilverfish = new EntitySilverfish(w);
			entitysilverfish.setLocationAndAngles((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
			w.spawnEntityInWorld(entitysilverfish);
			entitysilverfish.spawnExplosionParticle();
		}
	}

	private void handleEntityAction(PlayerActionEvent e) {
		if (e.getAction() == C0BPacketEntityAction.Action.OPEN_INVENTORY)
			if (e.getPlayer().ridingEntity instanceof EntityHorse)
				((EntityHorse) e.getPlayer().ridingEntity).openGUI(e.getPlayer());
		if (e.getAction() == C0BPacketEntityAction.Action.RIDING_JUMP)
			if (e.getPlayer().ridingEntity instanceof EntityHorse)
				((EntityHorse) e.getPlayer().ridingEntity).setJumpPower(e.getAux());
	}

	private void handlerTrackerUpdate(TrackerUpdateEvent e) {
		Entity entity = e.getEntry().trackedEntity;
		if (!(entity instanceof VanillaEntity)) return;
		VanillaEntity ve = (VanillaEntity) entity;
		Entity leashed = ve.getLeashedToEntity();
		if (leashed != null) {
			((MPlayer)e.getPlayer()).playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(1, entity, leashed));
		}
	}

	private void handleProjectileHit(ProjectileHitEvent e) {
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
			if (e.getBumpedInto().entityHit != null) {
				int i = 0;
				if (e.getBumpedInto().entityHit instanceof EntityBlaze) i = 3;

				e.getBumpedInto().entityHit.attackEntityFrom(DamageSource.causeThrownDamage(e.getThrowable(), e.getThrowable().getThrower()), (float) i);
			}
		}
	}

	private void handlePlayerEnderPearl(PlayerTeleportPearlEvent e) {
		EntityEnderPearl p = e.getPearl();
		Player m = e.getPlayer();
		if (p.rand.nextFloat() < 0.05F && p.worldObj.getGameRules().getBoolean("doMobSpawning")) {
			EntityEndermite entityendermite = new EntityEndermite(p.worldObj);
			entityendermite.setSpawnedByPlayer(true);
			entityendermite.setLocationAndAngles(m.posX, m.posY, m.posZ, m.rotationYaw, m.rotationPitch);
			p.worldObj.spawnEntityInWorld(entityendermite);
		}
	}


}
