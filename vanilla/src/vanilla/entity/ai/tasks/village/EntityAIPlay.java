package vanilla.entity.ai.tasks.village;

import net.minecraft.entity.EntityLivingBase;
import vanilla.entity.ai.RandomPositionGenerator;
import vanilla.entity.ai.tasks.EntityAIBase;
import vanilla.entity.passive.EntityVillager;
import net.minecraft.util.Vec3d;

import java.util.List;

public class EntityAIPlay extends EntityAIBase {

	private EntityVillager villagerObj;
	private EntityLivingBase targetVillager;
	private double speed;
	private int playTime;

	public EntityAIPlay(EntityVillager villagerObjIn, double speedIn) {
		this.villagerObj = villagerObjIn;
		this.speed = speedIn;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.villagerObj.getGrowingAge() >= 0) {
			return false;
		}
		if (this.villagerObj.getRNG().nextInt(400) != 0) {
			return false;
		}
		List<EntityVillager> list = this.villagerObj.worldObj.getEntitiesWithinAABB(EntityVillager.class, this.villagerObj.getEntityBoundingBox().expand(6.0D, 3.0D, 6.0D));
		double d0 = Double.MAX_VALUE;

		for (EntityVillager entityvillager : list) {
			if (entityvillager != this.villagerObj && !entityvillager.isPlaying() && entityvillager.getGrowingAge() < 0) {
				double d1 = entityvillager.getDistanceSqToEntity(this.villagerObj);

				if (d1 <= d0) {
					d0 = d1;
					this.targetVillager = entityvillager;
				}
			}
		}

		if (this.targetVillager == null) {
			Vec3d vec3D = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);

			if (vec3D == null) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return this.playTime > 0;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		if (this.targetVillager != null) {
			this.villagerObj.setPlaying(true);
		}

		this.playTime = 1000;
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		this.villagerObj.setPlaying(false);
		this.targetVillager = null;
	}

	/**
	 * Updates the task
	 */
	public void updateTask() {
		--this.playTime;

		if (this.targetVillager != null) {
			if (this.villagerObj.getDistanceSqToEntity(this.targetVillager) > 4.0D) {
				this.villagerObj.getNavigator().tryMoveToEntityLiving(this.targetVillager, this.speed);
			}
		} else if (this.villagerObj.getNavigator().noPath()) {
			Vec3d vec3D = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);

			if (vec3D == null) {
				return;
			}

			this.villagerObj.getNavigator().tryMoveToXYZ(vec3D.xCoord, vec3D.yCoord, vec3D.zCoord, this.speed);
		}
	}

}
