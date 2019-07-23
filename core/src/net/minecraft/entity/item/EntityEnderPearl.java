package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.resources.event.E;
import net.minecraft.resources.event.Events;
import net.minecraft.resources.event.events.PlayerEnderPearlEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ParticleType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEnderPearl extends EntityThrowable {

	private EntityLivingBase thrower;

	public EntityEnderPearl(World p_i46455_1_) {
		super(p_i46455_1_);
	}

	public EntityEnderPearl(World worldIn, EntityLivingBase thrower) {
		super(worldIn, thrower);
		this.thrower = thrower;
	}

	public EntityEnderPearl(World worldIn, double p_i1784_2_, double p_i1784_4_, double p_i1784_6_) {
		super(worldIn, p_i1784_2_, p_i1784_4_, p_i1784_6_);
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	protected void onImpact(MovingObjectPosition p_70184_1_) {
		EntityLivingBase entitylivingbase = this.getThrower();

		if (p_70184_1_.entityHit != null) {
			if (p_70184_1_.entityHit == this.thrower) {
				return;
			}

			p_70184_1_.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, entitylivingbase), 0.0F);
		}

		for (int i = 0; i < 32; ++i) {
			this.worldObj.spawnParticle(ParticleType.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ,
					this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
		}

		if (!this.worldObj.isClientSide) {
			if (entitylivingbase instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayermp = (EntityPlayerMP) entitylivingbase;


				if (entityplayermp.playerNetServerHandler.getNetworkManager().isChannelOpen() && entityplayermp.worldObj == this.worldObj && !entityplayermp.isPlayerSleeping()) {

					if (Events.eventPlayerEnderPearl.isUseful())
						Events.eventPlayerEnderPearl.call(new PlayerEnderPearlEvent(this, (EntityPlayerMP) entitylivingbase));

					if (entitylivingbase.isRiding()) entitylivingbase.mountEntity(null);

					entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
					entitylivingbase.fallDistance = 0.0F;
					entitylivingbase.attackEntityFrom(DamageSource.fall, 5.0F);
				}
			} else if (entitylivingbase != null) {
				entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
				entitylivingbase.fallDistance = 0.0F;
			}

			this.setDead();
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		EntityLivingBase entitylivingbase = this.getThrower();

		if (entitylivingbase instanceof EntityPlayer && !entitylivingbase.isEntityAlive()) {
			this.setDead();
		} else {
			super.onUpdate();
		}
	}

}
