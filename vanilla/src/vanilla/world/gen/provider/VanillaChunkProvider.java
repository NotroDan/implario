package vanilla.world.gen.provider;

import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraft.world.chunk.IChunkProvider;
import vanilla.entity.EnumCreatureType;

import java.util.List;

public interface VanillaChunkProvider extends IChunkProvider {

	List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos);

}
