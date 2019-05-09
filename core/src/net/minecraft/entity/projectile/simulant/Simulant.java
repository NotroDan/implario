package net.minecraft.entity.projectile.simulant;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public abstract class Simulant {
	static final Random rand = new Random();

	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	public Block inTile;
	private int inData;
	protected boolean inGround;
	public Entity shooter;
	private int ticksInGround;
	public int ticksInAir;
	public double posX, posY, posZ;
	private double prevPosX, prevPosY, prevPosZ;
	private float rotationYaw, rotationPitch;
	private float prevRotationYaw, prevRotationPitch;
	protected double motionX, motionZ, motionY;
	private AxisAlignedBB bb;
	public boolean destinated;
	public Entity entityHit;
	private final World world;
	public short inacc = -1;
	public int power;
	public double damage;

	public Simulant    (World worldIn, EntityLivingBase shooter, float velocity, float coef, float inaccuracy) {
		this.shooter = shooter;
		this.world = shooter.worldObj;

		this.setLocationAndAngles(shooter.posX, shooter.posY + (double) shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);

		this.posX -= (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
		this.posY -= 0.1D;
		this.posZ -= (double) (MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);

		this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * coef);
		this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * coef);
		this.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * coef);
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity * 1.5F, inaccuracy);
	}

	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ = z;
		this.rotationYaw = yaw;
		this.rotationPitch = pitch;
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	public void setPosition(double x, double y, double z)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		float f = 0.5F / 2.0F;
		float f1 = 0.5F;
		this.bb = new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f);
	}


	/**
	 * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
	 */
	public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
		float f = MathHelper.sqrt_double(x * x + y * y + z * z);
		x /= (double) f;
		y /= (double) f;
		z /= (double) f;
		x = x + 0.798 * 0.0075D * (double) inaccuracy;
		y = y + 0.798 * 0.0075D * (double) inaccuracy;
		z = z + 0.798 * 0.0075D * (double) inaccuracy;
		x *= (double) velocity;
		y *= (double) velocity;
		z *= (double) velocity;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float f1 = MathHelper.sqrt_double(x * x + z * z);
		this.prevRotationYaw = this.rotationYaw = (float) (MathHelper.func_181159_b(x, z) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float) (MathHelper.func_181159_b(y, (double) f1) * 180.0D / Math.PI);
		this.ticksInGround = 0;
	}

	public void onUpdate() {

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;

		if (this.posY < -64.0D) destinated = true;

		++this.ticksInAir;
		Vec3 from = new Vec3(this.posX, this.posY, this.posZ);
		Vec3 to = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		MovingObjectPosition movingobjectposition = this.world.rayTraceBlocks(from, to, false, ignoreNonOpaqueBlocks(), false);
		from = new Vec3(this.posX, this.posY, this.posZ);
		to = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

		if (movingobjectposition != null) {
			to = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
		}

		Entity entity = null;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, bb.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
		double d0 = 0.0D;

		for (int i = 0; i < list.size(); ++i) {
			Entity entity1 = list.get(i);

			if (entity1.canBeCollidedWith() && (entity1 != this.shooter || this.ticksInAir >= 5)) {
				float f1 = 0.3F;
				AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().expand((double) f1, (double) f1, (double) f1);
				MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(from, to);

				if (movingobjectposition1 != null) {
					double d1 = from.squareDistanceTo(movingobjectposition1.hitVec);

					if (d1 < d0 || d0 == 0.0D) {
						entity = entity1;
						d0 = d1;
					}
				}
			}
		}

		if (entity != null) {
			movingobjectposition = new MovingObjectPosition(entity);
		}

		if (movingobjectposition != null) {
			if (movingobjectposition.entityHit != null) {
				float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
				int l = MathHelper.ceiling_double_int(f2);
				destinated = true;
				entityHit = movingobjectposition.entityHit;
				double damage = 2;
				if (power > 0) damage += power * 0.5D + 0.5D;
				this.damage = getDamage(damage);


				this.motionX *= -0.1;
				this.motionY *= -0.1;
				this.motionZ *= -0.1;
				this.rotationYaw += 180.0F;
				this.prevRotationYaw += 180.0F;
//				this.ticksInAir = 0;
			} else {
				// Врезались в блок
				BlockPos blockpos1 = movingobjectposition.getBlockPos();
				this.xTile = blockpos1.getX();
				this.yTile = blockpos1.getY();
				this.zTile = blockpos1.getZ();
				IBlockState iblockstate1 = this.world.getBlockState(blockpos1);
				this.inTile = iblockstate1.getBlock();
				this.inData = this.inTile.getMetaFromState(iblockstate1);
				this.motionX = (double) (float) (movingobjectposition.hitVec.xCoord - this.posX);
				this.motionY = (double) (float) (movingobjectposition.hitVec.yCoord - this.posY);
				this.motionZ = (double) (float) (movingobjectposition.hitVec.zCoord - this.posZ);
				float f5 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
				this.posX -= this.motionX / (double) f5 * 0.05000000074505806D;
				this.posY -= this.motionY / (double) f5 * 0.05000000074505806D;
				this.posZ -= this.motionZ / (double) f5 * 0.05000000074505806D;
				this.inGround = true;
				destinated = true;

			}
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float) (MathHelper.func_181159_b(this.motionX, this.motionZ) * 180.0D / Math.PI);

		for (this.rotationPitch = (float) (MathHelper.func_181159_b(this.motionY,
				(double) f3) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {

		}

		while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
		float f4 = 0.99F;
		float f6 = getGravity();

		this.motionX *= (double) f4;
		this.motionY *= (double) f4;
		this.motionZ *= (double) f4;
		this.motionY -= (double) f6;
		this.setPosition(this.posX, this.posY, this.posZ);
	}


	public double getDamage(double baseDamage) {
		double damage = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
		return MathHelper.ceiling_double_int(damage * baseDamage);

	}

	protected abstract float getGravity();

	protected abstract boolean ignoreNonOpaqueBlocks();

}
