package vanilla.entity.ai.tasks;

import vanilla.entity.EntityCreature;
import vanilla.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3d;

public class EntityAIWander extends EntityAIBase {

	private EntityCreature entity;
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private double speed;
	private int executionChance;
	private boolean mustUpdate;

	public EntityAIWander(EntityCreature creatureIn, double speedIn) {
		this(creatureIn, speedIn, 120);
	}

	public EntityAIWander(EntityCreature creatureIn, double speedIn, int chance) {
		this.entity = creatureIn;
		this.speed = speedIn;
		this.executionChance = chance;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (!this.mustUpdate) {
			if (this.entity.getAge() >= 100)
				return false;

			if (this.entity.getRNG().nextInt(this.executionChance) != 0) return false;
		}

		Vec3d vec3D = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);

		if (vec3D == null) {
			return false;
		}
		this.xPosition = vec3D.xCoord;
		this.yPosition = vec3D.yCoord;
		this.zPosition = vec3D.zCoord;
		this.mustUpdate = false;
		return true;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !this.entity.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
	}

	/**
	 * Makes task to bypass chance
	 */
	public void makeUpdate() {
		this.mustUpdate = true;
	}

	/**
	 * Changes task random possibility for execution
	 */
	public void setExecutionChance(int newchance) {
		this.executionChance = newchance;
	}

}
