package vanilla.world;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.datapacks.DimensionManager;

public class VanillaDimensionManager implements DimensionManager {

	@Override
	public WorldProvider generate(int dimension) {
		return dimension == -1 ? new WorldProviderHell() : dimension == 0 ? new WorldProviderSurface() : dimension == 1 ? new WorldProviderEnd() : null;
	}

}
