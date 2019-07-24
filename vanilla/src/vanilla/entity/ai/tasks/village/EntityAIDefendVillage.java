package vanilla.entity.ai.tasks.village;

import net.minecraft.entity.EntityLivingBase;
import vanilla.entity.ai.tasks.EntityAITarget;
import vanilla.entity.monster.EntityCreeper;
import vanilla.entity.monster.EntityIronGolem;
import vanilla.world.gen.feature.village.Village;

public class EntityAIDefendVillage extends EntityAITarget {

	EntityIronGolem irongolem;

	/**
	 * The aggressor of the iron golem's village which is now the golem's attack target.
	 */
	EntityLivingBase villageAgressorTarget;

	public EntityAIDefendVillage(EntityIronGolem ironGolemIn) {
		super(ironGolemIn, false, true);
		this.irongolem = ironGolemIn;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		Village village = this.irongolem.getVillage();

		if (village == null) {
			return false;
		}
		this.villageAgressorTarget = village.findNearestVillageAggressor(this.irongolem);

		if (this.villageAgressorTarget instanceof EntityCreeper) {
			return false;
		}
		if (!this.isSuitableTarget(this.villageAgressorTarget, false)) {
			if (this.taskOwner.getRNG().nextInt(20) == 0) {
				this.villageAgressorTarget = village.getNearestTargetPlayer(this.irongolem);
				return this.isSuitableTarget(this.villageAgressorTarget, false);
			}
			return false;
		}
		return true;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.irongolem.setAttackTarget(this.villageAgressorTarget);
		super.startExecuting();
	}

}
