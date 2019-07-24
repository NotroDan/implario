package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityEnderCrystal extends Entity {

	/**
	 * Used to create the rotation animation when rendering the crystal.
	 */
	public int innerRotation;
	public int health;

	public EntityEnderCrystal(World worldIn) {
		super(worldIn);
		this.preventEntitySpawning = true;
		this.setSize(2.0F, 2.0F);
		this.health = 5;
		this.innerRotation = this.rand.nextInt(100000);
	}

	public EntityEnderCrystal(World worldIn, double p_i1699_2_, double p_i1699_4_, double p_i1699_6_) {
		this(worldIn);
		this.setPosition(p_i1699_2_, p_i1699_4_, p_i1699_6_);
	}


	@Override
	public int getUpdateFrequency() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getTrackingRange() {
		return 256;
	}

	@Override
	public boolean sendVelocityUpdates() {
		return false;
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
		this.dataWatcher.addObject(8, this.health);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		++this.innerRotation;
		this.dataWatcher.updateObject(8, this.health);
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.posY);
		int k = MathHelper.floor_double(this.posZ);

		if (this.worldObj.getBlockState(new BlockPos(i, j, k)).getBlock() != Blocks.fire)
			this.worldObj.setBlockState(new BlockPos(i, j, k), Blocks.fire.getDefaultState());
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith() {
		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) return false;
		if (this.isDead || this.worldObj.isClientSide) return true;
		this.health = 0;
		if (this.health > 0) return true;

		this.setDead();
		if (!this.worldObj.isClientSide) this.worldObj.createExplosion(null, this.posX, this.posY, this.posZ, 6.0F, true);

		return true;
	}


	@Override
	public boolean doTracking() {
		return true;
	}

}
