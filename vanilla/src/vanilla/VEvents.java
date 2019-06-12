package vanilla;

import net.minecraft.block.FenceClickedEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.resources.Loadable;
import net.minecraft.resources.Registrar;
import net.minecraft.resources.event.events.*;
import net.minecraft.resources.event.events.block.BlockDropEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import vanilla.entity.VanillaEntity;
import vanilla.entity.boss.DragonPartRedirecter;
import vanilla.entity.monster.EntityBlaze;
import vanilla.entity.monster.EntityEndermite;
import vanilla.entity.monster.EntitySilverfish;
import vanilla.entity.passive.EntityChicken;
import vanilla.entity.passive.EntityHorse;
import vanilla.item.ItemLead;
import vanilla.world.SleepChecker;

public class VEvents implements Loadable {

	@Override
	public void load(Registrar registrar) {
		registrar.regListener(DamageByEntityEvent.class, new DragonPartRedirecter());
		registrar.regListener(TrySleepEvent.class, new SleepChecker());
		registrar.regListener(PlayerEntityActionEvent.class, this::handleEntityAction);
		registrar.regListener(UpdateEntityToSpectatorEvent.class, this::handleEntityUpdateToSpectator);
		registrar.regListener(ProjectileHitEvent.class, this::handleProjectileHit);
		registrar.regListener(PlayerEnderPearlEvent.class, this::handlePlayerEnderPearl);
		registrar.regListener(FenceClickedEvent.class, this::handleFenceClick);
		registrar.regListener(BlockDropEvent.class, this::handleBlockDrop);
	}

	private void handleFenceClick(FenceClickedEvent e) {e.returnValue = ItemLead.attachToFence(e.getPlayer(), e.getWorld(), e.getPos());}

	private void handleBlockDrop(BlockDropEvent e) {
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
