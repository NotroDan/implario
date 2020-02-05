package vanilla.entity.ai.tasks;

import net.minecraft.entity.Entity;
import vanilla.entity.EntityCreature;
import vanilla.entity.ai.RandomPositionGenerator;
import vanilla.entity.ai.pathfinding.PathEntity;
import vanilla.entity.ai.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Vec3d;

import java.util.List;
import java.util.function.Predicate;

public class EntityAIAvoidEntity<T extends Entity> extends EntityAIBase {

	private final Predicate<Entity> canBeSeenSelector;

	/**
	 * The entity we are attached to
	 */
	protected EntityCreature theEntity;
	private double farSpeed;
	private double nearSpeed;
	protected T closestLivingEntity;
	private float avoidDistance;

	/**
	 * The PathEntity of our entity
	 */
	private PathEntity entityPathEntity;

	/**
	 * The PathNavigate of our entity
	 */
	private PathNavigate entityPathNavigate;
	private Class<T> field_181064_i;
	private Predicate<? super T> avoidTargetSelector;

	public EntityAIAvoidEntity(EntityCreature p_i46404_1_, Class<T> p_i46404_2_, float p_i46404_3_, double p_i46404_4_, double p_i46404_6_) {
		this(p_i46404_1_, p_i46404_2_, (__) -> true, p_i46404_3_, p_i46404_4_, p_i46404_6_);
	}

	public EntityAIAvoidEntity(EntityCreature p_i46405_1_, Class<T> p_i46405_2_, Predicate<? super T> p_i46405_3_, float p_i46405_4_, double p_i46405_5_, double p_i46405_7_) {
		this.canBeSeenSelector = new Predicate<Entity>() {
			public boolean test(Entity p_apply_1_) {
				return p_apply_1_.isEntityAlive() && EntityAIAvoidEntity.this.theEntity.getEntitySenses().canSee(p_apply_1_);
			}
		};
		this.theEntity = p_i46405_1_;
		this.field_181064_i = p_i46405_2_;
		this.avoidTargetSelector = p_i46405_3_;
		this.avoidDistance = p_i46405_4_;
		this.farSpeed = p_i46405_5_;
		this.nearSpeed = p_i46405_7_;
		this.entityPathNavigate = p_i46405_1_.getNavigator();
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		List<T> list = this.theEntity.worldObj.getEntitiesWithinAABB(this.field_181064_i, this.theEntity.getEntityBoundingBox().expand((double) this.avoidDistance, 3.0D, (double) this.avoidDistance),
				(entity) -> EntitySelectors.NOT_SPECTATING.and(this.canBeSeenSelector).test(entity) && this.avoidTargetSelector.test(entity));

		if (list.isEmpty()) {
			return false;
		}
		this.closestLivingEntity = list.get(0);
		Vec3d vec3D = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 16, 7, new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

		if (vec3D == null) {
			return false;
		}
		if (this.closestLivingEntity.getDistanceSq(vec3D.xCoord, vec3D.yCoord, vec3D.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.theEntity)) {
			return false;
		}
		this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3D.xCoord, vec3D.yCoord, vec3D.zCoord);
		return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(vec3D);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return !this.entityPathNavigate.noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		this.closestLivingEntity = null;
	}

	/**
	 * Updates the task
	 */
	public void updateTask() {
		if (this.theEntity.getDistanceSqToEntity(this.closestLivingEntity) < 49.0D) {
			this.theEntity.getNavigator().setSpeed(this.nearSpeed);
		} else {
			this.theEntity.getNavigator().setSpeed(this.farSpeed);
		}
	}

}
