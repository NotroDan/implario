package vanilla.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Profiler;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerExtra;
import net.minecraft.world.WorldService;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldInfo;

public class VanillaWorldService extends WorldService<VanillaWorldServer> {

	private VanillaWorldServer[] worlds = new VanillaWorldServer[3];

	public VanillaWorldService(MinecraftServer server) {
		super(server);
		dimensionAmount = 3;
	}

	@Override
	public WorldServer loadDim(int dim, String worldName, WorldInfo info, WorldSettings settings) {
		int level = dim == 0 ? 0 : dim == 1 ? -1 : dim == 2 ? 1 : dim;
		if (level == 0) {
			worlds[dim] = (VanillaWorldServer) new VanillaWorldServer(server, getSaveHandler(worldName), info, level, Profiler.in).init();
			worlds[dim].initialize(settings);
			return worlds[dim];
		}
		return worlds[dim] = (VanillaWorldServer) new WorldServerExtra(server, getSaveHandler(worldName), level, worlds[0], Profiler.in).init();
	}

	@Override
	public VanillaWorldServer[] getAll() {
		return worlds;
	}

	@Override
	public VanillaWorldServer getWorld(int dim) {
		return worlds[dim];
	}

}

