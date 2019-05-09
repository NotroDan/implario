package net.minecraft.entity.projectile.simulant;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class SimulantSimpleProjectile extends Simulant {

	public SimulantSimpleProjectile(World worldIn, EntityLivingBase shooter, float inaccuracy) {
		super(worldIn, shooter, 1, 0.4F, inaccuracy);
	}

	@Override
	protected boolean ignoreNonOpaqueBlocks() {
		return false;
	}

	@Override
	protected float getGravity() {
		return 0.03F;
	}

}
