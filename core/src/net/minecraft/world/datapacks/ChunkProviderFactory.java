package net.minecraft.world.datapacks;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

public interface ChunkProviderFactory {

	IChunkProvider generate(WorldProvider p);

}
