package vanilla;

import net.minecraft.block.FenceClickedEvent;
import net.minecraft.block.material.Material;
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
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.resources.Registrar;
import net.minecraft.resources.ServerSideLoadable;
import net.minecraft.resources.event.Events;
import net.minecraft.resources.event.events.*;
import net.minecraft.resources.event.events.block.BlockDropEvent;
import net.minecraft.resources.event.events.player.PlayerFallEvent;
import net.minecraft.resources.event.events.player.PlayerItemDropEvent;
import net.minecraft.resources.event.events.player.PlayerMoveEvent;
import net.minecraft.resources.event.events.player.PlayerTickEvent;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vanilla.entity.VanillaEntity;
import vanilla.entity.monster.EntityBlaze;
import vanilla.entity.monster.EntityEndermite;
import vanilla.entity.monster.EntitySilverfish;
import vanilla.entity.passive.EntityChicken;
import vanilla.entity.passive.EntityHorse;
import vanilla.entity.passive.EntityPig;
import vanilla.item.ItemLead;
import vanilla.world.SleepChecker;

public class VEvents implements ServerSideLoadable {

	@Override
	public void load(Registrar registrar) {

		Events.eventPlayerMove.add(this::handlePlayerMove);
		Events.eventMountMove.add(this::handleMountMove);
		Events.eventPlayerTick.add(this::handlePlayerTick);
		Events.eventPlayerFall.add(this::handlePlayerFall);
		Events.eventPlayerDisconnect.add(e -> e.getPlayer().triggerAchievement(StatList.leaveGameStat));
		Events.eventPlayerJump.add(e -> e.getPlayer().triggerAchievement(StatList.jumpStat));
		Events.eventPlayerItemDrop.add(this::handleItemDrop);
		Events.eventPlayerDeath.add(e -> e.getPlayer().triggerAchievement(StatList.deathsStat));
		Events.eventPlayerSleep.add(new SleepChecker());


		registrar.regListener(PlayerEntityActionEvent.class, this::handleEntityAction);
		registrar.regListener(UpdateEntityToSpectatorEvent.class, this::handleEntityUpdateToSpectator);
		registrar.regListener(ProjectileHitEvent.class, this::handleProjectileHit);
		registrar.regListener(PlayerEnderPearlEvent.class, this::handlePlayerEnderPearl);
		registrar.regListener(FenceClickedEvent.class, this::handleFenceClick);
		registrar.regListener(BlockDropEvent.class, this::handleBlockDrop);
	}

	private void handleItemDrop(PlayerItemDropEvent e) {
		if (e.isTraceItem()) e.getPlayer().triggerAchievement(StatList.dropStat);
	}

	private void handlePlayerFall(PlayerFallEvent e) {
		EntityPlayer p = e.getPlayer();
		float distance = e.getDistance();
		if (distance >= 2.0F) p.addStat(StatList.distanceFallenStat, (int) Math.round((double) distance * 100.0D));
	}

	private void handlePlayerTick(PlayerTickEvent e) {
		if (e.getPlayer().worldObj.isClientSide) return;
		e.getPlayer().triggerAchievement(StatList.minutesPlayedStat);
		if (e.getPlayer().isEntityAlive()) e.getPlayer().triggerAchievement(StatList.timeSinceDeathStat);
	}

	private void handleMountMove(MountMoveEvent e) {
		EntityPlayer player = e.getPlayer();
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

		EntityPlayer p = event.getPlayer();

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

	private void handleFenceClick(FenceClickedEvent e) {e.returnValue = ItemLead.attachToFence(e.getPlayer(), e.getWorld(), e.getPos());}

	private void handleBlockDrop(BlockDropEvent e) {
		World w = e.getWorld();
		BlockPos pos = e.getPosition();
		if (e.getBlock().getBlock() == Blocks.monster_egg) {
			e.cancelDefaultDrop();
			if (!w.isClientSide && w.getGameRules().getBoolean("doTileDrops")) {
				EntitySilverfish entitysilverfish = new EntitySilverfish(w);
				entitysilverfish.setLocationAndAngles((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
				w.spawnEntityInWorld(entitysilverfish);
				entitysilverfish.spawnExplosionParticle();
			}
		}
	}


	private void handleEntityAction(PlayerEntityActionEvent e) {
		if (e.getAction() == C0BPacketEntityAction.Action.OPEN_INVENTORY)
			if (e.getPlayer().ridingEntity instanceof EntityHorse)
				((EntityHorse) e.getPlayer().ridingEntity).openGUI(e.getPlayer());
		if (e.getAction() == C0BPacketEntityAction.Action.RIDING_JUMP)
			if (e.getPlayer().ridingEntity instanceof EntityHorse)
				((EntityHorse) e.getPlayer().ridingEntity).setJumpPower(e.getAux());
	}

	private void handleEntityUpdateToSpectator(UpdateEntityToSpectatorEvent e) {
		Entity entity = e.getTrackerEntry().trackedEntity;
		if (!(entity instanceof VanillaEntity)) return;
		VanillaEntity ve = (VanillaEntity) entity;
		Entity leashed = ve.getLeashedToEntity();
		if (leashed != null) {
			e.getPlayer().playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(1, entity, leashed));
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
			if (e.getObject().entityHit != null) {
				int i = 0;
				if (e.getObject().entityHit instanceof EntityBlaze) i = 3;

				e.getObject().entityHit.attackEntityFrom(DamageSource.causeThrownDamage(e.getThrowable(), e.getThrowable().getThrower()), (float) i);
			}
		}
	}

	private void handlePlayerEnderPearl(PlayerEnderPearlEvent e) {
		EntityEnderPearl p = e.getPearl();
		EntityPlayerMP m = e.getPlayer();
		if (p.rand.nextFloat() < 0.05F && p.worldObj.getGameRules().getBoolean("doMobSpawning")) {
			EntityEndermite entityendermite = new EntityEndermite(p.worldObj);
			entityendermite.setSpawnedByPlayer(true);
			entityendermite.setLocationAndAngles(m.posX, m.posY, m.posZ, m.rotationYaw, m.rotationPitch);
			p.worldObj.spawnEntityInWorld(entityendermite);
		}
	}


}
