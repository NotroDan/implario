package net.minecraft.world.biome;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.WeightedRandom;

public class SpawnListEntry extends WeightedRandom.Item {

	public Class<? extends EntityLivingBase> entityClass;
	public int minGroupCount;
	public int maxGroupCount;

	public SpawnListEntry(Class<? extends EntityLivingBase> entityclassIn, int weight, int groupCountMin, int groupCountMax) {
		super(weight);//TODO: рефлексия зло
		this.entityClass = entityclassIn;
		this.minGroupCount = groupCountMin;
		this.maxGroupCount = groupCountMax;
	}

	public String toString() {
		return this.entityClass.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
	}

}
