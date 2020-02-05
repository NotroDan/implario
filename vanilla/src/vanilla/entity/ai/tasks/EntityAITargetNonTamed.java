package vanilla.entity.ai.tasks;

import net.minecraft.entity.EntityLivingBase;
import vanilla.entity.passive.EntityTameable;

import java.util.function.Predicate;

public class EntityAITargetNonTamed<T extends EntityLivingBase> extends EntityAINearestAttackableTarget {

	private EntityTameable theTameable;

	public EntityAITargetNonTamed(EntityTameable entityIn, Class<T> classTarget, boolean checkSight, Predicate<? super T> targetSelector) {
		super(entityIn, classTarget, 10, checkSight, false, targetSelector);
		this.theTameable = entityIn;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		return !this.theTameable.isTamed() && super.shouldExecute();
	}

}
