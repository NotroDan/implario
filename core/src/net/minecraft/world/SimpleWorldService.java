package net.minecraft.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class SimpleWorldService extends WorldService<WorldServer> {


	private WorldServer world;

	public SimpleWorldService(MinecraftServer server) {
		super(server);
		dimensionAmount = 1;
	}

	@Override
	public WorldServer getWorld(int dim) {
		return world;
	}

	@Override
	public WorldServer loadDim(int dim, String worldName, WorldInfo info, WorldSettings settings, ISaveHandler isavehandler) {
		world = (WorldServer) new WorldServer(server, isavehandler, info, dim, MinecraftServer.profiler).init();
		world.initialize(settings);
		return world;
	}

	@Override
	public WorldServer[] getAll() {
		return new WorldServer[] {world};
	}

}
