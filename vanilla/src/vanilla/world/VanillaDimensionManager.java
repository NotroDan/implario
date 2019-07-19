package vanilla.world;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;

public class VanillaDimensionManager {

	public static WorldProvider generate(int dimension) {
		return dimension == -1 ? new WorldProviderHell() : dimension == 0 ? new WorldProviderSurface() : dimension == 1 ? new WorldProviderEnd() : null;
	}

}
