package vanilla.entity.ai.tasks;

import vanilla.entity.EntityCreature;
import vanilla.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3d;

public class EntityAIPanic extends EntityAIBase {

	private EntityCreature theEntityCreature;
	protected double speed;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIPanic(EntityCreature creature, double speedIn) {
		this.theEntityCreature = creature;
		this.speed = speedIn;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.theEntityCreature.getAITarget() == null && !this.theEntityCreature.isBurning()) {
			return false;
		}
		Vec3d vec3D = RandomPositionGenerator.findRandomTarget(this.theEntityCreature, 5, 4);

		if (vec3D == null) {
			return false;
		}
		this.randPosX = vec3D.xCoord;
		this.randPosY = vec3D.yCoord;
		this.randPosZ = vec3D.zCoord;
		return true;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.theEntityCreature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !this.theEntityCreature.getNavigator().noPath();
	}

}
