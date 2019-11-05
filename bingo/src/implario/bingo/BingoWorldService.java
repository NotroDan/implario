package implario.bingo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.datapacks.SimpleDimensionManager;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import vanilla.world.VanillaWorldServer;

public class BingoWorldService extends WorldService<WorldServer> {

	private WorldServer[] worlds = new WorldServer[2];

	public BingoWorldService(MinecraftServer server) {
		super(server);
		dimensionAmount = 2;
	}

	@Override
	public WorldServer loadDim(int dim, String worldName, WorldInfo info, WorldSettings settings, ISaveHandler isavehandler) {
		if (dim == 0) return worlds[dim] = (WorldServer) new BingoWorldServer(server, info, settings, isavehandler, dim).init();
		else return worlds[dim] = (WorldServer) new VanillaWorldServer(server, isavehandler, info, dim, MinecraftServer.profiler).init();
	}

	public static WorldProvider generate(int dimension) {
		return dimension == 0 ? new SimpleDimensionManager(dimension) : new WorldProviderSurface();
	}

	@Override
	public WorldServer[] getAll() {
		return worlds;
	}

	@Override
	public WorldServer getWorld(int dim) {
		return worlds[dim];
	}
}
