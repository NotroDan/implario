package net.minecraft.world.datapacks;

import net.minecraft.world.WorldProvider;

public interface DimensionManager {

	DimensionManager SIMPLE = SimpleDimensionManager::new;

	WorldProvider generate(int dimension);

}
