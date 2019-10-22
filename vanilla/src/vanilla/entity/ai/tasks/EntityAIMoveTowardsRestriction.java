package vanilla.entity.ai.tasks;

import vanilla.entity.EntityCreature;
import vanilla.entity.ai.RandomPositionGenerator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3d;

public class EntityAIMoveTowardsRestriction extends EntityAIBase {

	private EntityCreature theEntity;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private double movementSpeed;

	public EntityAIMoveTowardsRestriction(EntityCreature creatureIn, double speedIn) {
		this.theEntity = creatureIn;
		this.movementSpeed = speedIn;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.theEntity.isWithinHomeDistanceCurrentPosition()) {
			return false;
		}
		BlockPos blockpos = this.theEntity.getHomePosition();
		Vec3d vec3D = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, new Vec3d((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ()));

		if (vec3D == null) {
			return false;
		}
		this.movePosX = vec3D.xCoord;
		this.movePosY = vec3D.yCoord;
		this.movePosZ = vec3D.zCoord;
		return true;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !this.theEntity.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
	}

}
