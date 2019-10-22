package net.minecraft.util;

import net.minecraft.entity.Entity;

public class MovingObjectPosition {

	private BlockPos blockPos;

	/**
	 * What type of ray trace hit was this? 0 = block, 1 = entity
	 */
	public MovingObjectPosition.MovingObjectType typeOfHit;
	public EnumFacing sideHit;

	/**
	 * The vector position of the hit
	 */
	public Vec3d hitVec;

	/**
	 * The hit entity
	 */
	public Entity entityHit;

	public MovingObjectPosition(Vec3d hitVecIn, EnumFacing facing, BlockPos blockPosIn) {
		this(MovingObjectPosition.MovingObjectType.BLOCK, hitVecIn, facing, blockPosIn);
	}

	public MovingObjectPosition(Vec3d p_i45552_1_, EnumFacing facing) {
		this(MovingObjectPosition.MovingObjectType.BLOCK, p_i45552_1_, facing, BlockPos.ORIGIN);
	}

	public MovingObjectPosition(Entity p_i2304_1_) {
		this(p_i2304_1_, new Vec3d(p_i2304_1_.posX, p_i2304_1_.posY, p_i2304_1_.posZ));
	}

	public MovingObjectPosition(MovingObjectPosition.MovingObjectType typeOfHitIn, Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
		this.typeOfHit = typeOfHitIn;
		this.blockPos = blockPosIn;
		this.sideHit = sideHitIn;
		this.hitVec = new Vec3d(hitVecIn.xCoord, hitVecIn.yCoord, hitVecIn.zCoord);
	}

	public MovingObjectPosition(Entity entityHitIn, Vec3d hitVecIn) {
		this.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
		this.entityHit = entityHitIn;
		this.hitVec = hitVecIn;
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public String toString() {
		return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entityHit + '}';
	}

	public static enum MovingObjectType {
		MISS,
		BLOCK,
		ENTITY;
	}

}
