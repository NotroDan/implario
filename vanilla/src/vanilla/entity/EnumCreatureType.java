package vanilla.entity;

import net.minecraft.entity.IAnimals;
import vanilla.entity.monster.IMob;
import vanilla.entity.passive.EntityAmbientCreature;
import vanilla.entity.passive.EntityAnimal;
import vanilla.entity.passive.EntityWaterMob;

public enum EnumCreatureType {
	MONSTER(IMob.class, 70, false, false),
	CREATURE(EntityAnimal.class, 10, true, true),
	AMBIENT(EntityAmbientCreature.class, 15, true, false),
	WATER_CREATURE(EntityWaterMob.class, 5, true, false);

	private final Class<? extends IAnimals> creatureClass;
	private final int maxNumberOfCreature;

	/**
	 * A flag indicating whether this creature type is peaceful.
	 */
	private final boolean isPeacefulCreature;

	/**
	 * Whether this creature type is an animal.
	 */
	private final boolean isAnimal;

	private EnumCreatureType(Class<? extends IAnimals> creatureClassIn, int maxNumberOfCreatureIn, boolean isPeacefulCreatureIn, boolean isAnimalIn) {
		this.creatureClass = creatureClassIn;
		this.maxNumberOfCreature = maxNumberOfCreatureIn;
		this.isPeacefulCreature = isPeacefulCreatureIn;
		this.isAnimal = isAnimalIn;
	}

	public Class<? extends IAnimals> getCreatureClass() {
		return this.creatureClass;
	}

	public int getMaxNumberOfCreature() {
		return this.maxNumberOfCreature;
	}

	/**
	 * Gets whether or not this creature type is peaceful.
	 */
	public boolean getPeacefulCreature() {
		return this.isPeacefulCreature;
	}

	/**
	 * Return whether this creature type is an animal.
	 */
	public boolean getAnimal() {
		return this.isAnimal;
	}
}
