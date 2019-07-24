package net.minecraft.entity;

import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface IComplexEntityRoot {

    World getWorld();

    boolean attackEntityFromPart(IComplexEntityBranch part, DamageSource source, float damage);

}
