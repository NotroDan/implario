package vanilla.entity.ai.tasks.village;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3d;
import vanilla.entity.EntityCreature;
import vanilla.entity.ai.RandomPositionGenerator;
import vanilla.entity.ai.tasks.EntityAIBase;
import vanilla.world.gen.feature.village.Village;
import vanilla.world.gen.feature.village.VillageCollection;
import vanilla.world.gen.feature.village.VillageDoorInfo;

public class EntityAIMoveIndoors extends EntityAIBase {

	private EntityCreature entityObj;
	private VillageDoorInfo doorInfo;
	private int insidePosX = -1;
	private int insidePosZ = -1;

	public EntityAIMoveIndoors(EntityCreature entityObjIn) {
		this.entityObj = entityObjIn;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		BlockPos blockpos = new BlockPos(this.entityObj);

		if ((!this.entityObj.worldObj.isDaytime() || this.entityObj.worldObj.isRaining() && !this.entityObj.worldObj.getBiomeGenForCoords(
				blockpos).canSpawnLightningBolt()) && !this.entityObj.worldObj.provider.getHasNoSky()) {
			if (this.entityObj.getRNG().nextInt(50) != 0) {
				return false;
			}
			if (this.insidePosX != -1 && this.entityObj.getDistanceSq((double) this.insidePosX, this.entityObj.posY, (double) this.insidePosZ) < 4.0D) {
				return false;
			}
			Village village = VillageCollection.get(this.entityObj.worldObj).getNearestVillage(blockpos, 14);

			if (village == null) {
				return false;
			}
			this.doorInfo = village.getDoorInfo(blockpos);
			return this.doorInfo != null;
		}
		return false;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !this.entityObj.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.insidePosX = -1;
		BlockPos blockpos = this.doorInfo.getInsideBlockPos();
		int i = blockpos.getX();
		int j = blockpos.getY();
		int k = blockpos.getZ();

		if (this.entityObj.getDistanceSq(blockpos) > 256.0D) {
			Vec3d vec3D = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, new Vec3d((double) i + 0.5D, (double) j, (double) k + 0.5D));

			if (vec3D != null) {
				this.entityObj.getNavigator().tryMoveToXYZ(vec3D.xCoord, vec3D.yCoord, vec3D.zCoord, 1.0D);
			}
		} else {
			this.entityObj.getNavigator().tryMoveToXYZ((double) i + 0.5D, (double) j, (double) k + 0.5D, 1.0D);
		}
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		this.insidePosX = this.doorInfo.getInsideBlockPos().getX();
		this.insidePosZ = this.doorInfo.getInsideBlockPos().getZ();
		this.doorInfo = null;
	}

}
