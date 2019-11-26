package implario.bingo;

import net.minecraft.resources.Datapack;
import net.minecraft.resources.Domain;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class Bingo extends Datapack {

	public static final Domain DOMAIN = new Domain("bingo");

	public Bingo() {
		super(DOMAIN);
	}


	@Override
	public void preinit() {
		registrar.replaceProvider(MinecraftServer.WORLD_SERVICE_PROVIDER, BingoWorldService::new);
		registrar.replaceProvider(World.DIMENSION_PROVIDER, BingoWorldService::generate);
	}

	@Override
	public void init() {
		registrar.registerCommand(new CommandBingo());
	}

}
