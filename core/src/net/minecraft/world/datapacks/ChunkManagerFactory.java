package net.minecraft.world.datapacks;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.IChunkBiomer;

public interface ChunkManagerFactory {
	IChunkBiomer generate(WorldProvider p, long seed, WorldType type, String go);
}
