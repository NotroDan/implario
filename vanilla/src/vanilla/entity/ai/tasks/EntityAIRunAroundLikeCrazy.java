package vanilla.entity.ai.tasks;

import net.minecraft.entity.Entity;
import vanilla.entity.ai.RandomPositionGenerator;
import vanilla.entity.passive.EntityHorse;
import net.minecraft.entity.player.Player;
import net.minecraft.util.Vec3d;

public class EntityAIRunAroundLikeCrazy extends EntityAIBase {

	private EntityHorse horseHost;
	private double speed;
	private double targetX;
	private double targetY;
	private double targetZ;

	public EntityAIRunAroundLikeCrazy(EntityHorse horse, double speedIn) {
		this.horseHost = horse;
		this.speed = speedIn;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (!this.horseHost.isTame() && this.horseHost.riddenByEntity != null) {
			Vec3d vec3D = RandomPositionGenerator.findRandomTarget(this.horseHost, 5, 4);

			if (vec3D == null) {
				return false;
			}
			this.targetX = vec3D.xCoord;
			this.targetY = vec3D.yCoord;
			this.targetZ = vec3D.zCoord;
			return true;
		}
		return false;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.horseHost.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !this.horseHost.getNavigator().noPath() && this.horseHost.riddenByEntity != null;
	}

	/**
	 * Updates the task
	 */
	public void updateTask() {
		if (this.horseHost.getRNG().nextInt(50) == 0) {
			if (this.horseHost.riddenByEntity instanceof Player) {
				int i = this.horseHost.getTemper();
				int j = this.horseHost.getMaxTemper();

				if (j > 0 && this.horseHost.getRNG().nextInt(j) < i) {
					this.horseHost.setTamedBy((Player) this.horseHost.riddenByEntity);
					this.horseHost.worldObj.setEntityState(this.horseHost, (byte) 7);
					return;
				}

				this.horseHost.increaseTemper(5);
			}

			this.horseHost.riddenByEntity.mountEntity((Entity) null);
			this.horseHost.riddenByEntity = null;
			this.horseHost.makeHorseRearWithSound();
			this.horseHost.worldObj.setEntityState(this.horseHost, (byte) 6);
		}
	}

}
