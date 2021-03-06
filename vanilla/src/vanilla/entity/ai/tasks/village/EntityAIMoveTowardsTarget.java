package vanilla.entity.ai.tasks.village;

import vanilla.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import vanilla.entity.ai.RandomPositionGenerator;
import vanilla.entity.ai.tasks.EntityAIBase;
import net.minecraft.util.Vec3d;

public class EntityAIMoveTowardsTarget extends EntityAIBase {

	private EntityCreature theEntity;
	private EntityLivingBase targetEntity;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private double speed;

	/**
	 * If the distance to the target entity is further than this, this AI task will not run.
	 */
	private float maxTargetDistance;

	public EntityAIMoveTowardsTarget(EntityCreature creature, double speedIn, float targetMaxDistance) {
		this.theEntity = creature;
		this.speed = speedIn;
		this.maxTargetDistance = targetMaxDistance;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		this.targetEntity = this.theEntity.getAttackTarget();

		if (this.targetEntity == null) {
			return false;
		}
		if (this.targetEntity.getDistanceSqToEntity(this.theEntity) > (double) (this.maxTargetDistance * this.maxTargetDistance)) {
			return false;
		}
		Vec3d vec3D = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, new Vec3d(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ));

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
		return !this.theEntity.getNavigator().noPath() && this.targetEntity.isEntityAlive() && this.targetEntity.getDistanceSqToEntity(
				this.theEntity) < (double) (this.maxTargetDistance * this.maxTargetDistance);
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		this.targetEntity = null;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
	}

}
