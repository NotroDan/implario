package net.minecraft.entity.projectile.simulant;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class SimulantArrow extends Simulant {

	public SimulantArrow(World worldIn, EntityLivingBase shooter, float velocity, float inaccuracy) {
		super(worldIn, shooter, velocity, 1, inaccuracy);
	}

	@Override
	protected boolean ignoreNonOpaqueBlocks() {
		return true;
	}


	@Override
	protected float getGravity() {
		return 0.05F;
	}

}
