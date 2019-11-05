package implario.bingo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class BingoWorldServer extends WorldServer {

	public BingoWorldServer(MinecraftServer server, WorldInfo info, WorldSettings settings, ISaveHandler isavehandler, int dim) {
		super(server, isavehandler, info, dim, MinecraftServer.profiler);
	}

}
