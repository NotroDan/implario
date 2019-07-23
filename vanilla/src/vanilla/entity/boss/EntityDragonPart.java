package vanilla.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IComplexEntityBranch;
import net.minecraft.entity.IComplexEntityRoot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityDragonPart extends Entity implements IComplexEntityBranch {

	/**
	 * The dragon entity this dragon part belongs to
	 */
	public final IComplexEntityRoot entityDragonObj;
	public final String partName;

	public EntityDragonPart(IComplexEntityRoot parent, String partName, float base, float sizeHeight) {
		super(parent.getWorld());
		this.setSize(base, sizeHeight);
		this.entityDragonObj = parent;
		this.partName = partName;
	}

	@Override
	public IComplexEntityRoot getRoot() {
		return entityDragonObj;
	}

	protected void entityInit() {
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith() {
		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return !this.isEntityInvulnerable(source) && this.entityDragonObj.attackEntityFromPart(this, source, amount);
	}

	/**
	 * Returns true if Entity argument is equal to this Entity
	 */
	public boolean isEntityEqual(Entity entityIn) {
		return this == entityIn || this.entityDragonObj == entityIn;
	}

}
