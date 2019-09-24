package net.minecraft.client;

import net.minecraft.client.resources.ClientRegistrar;
import net.minecraft.client.resources.ClientSideDatapack;
import net.minecraft.client.settings.Settings;
import net.minecraft.resources.Datapack;
import net.minecraft.server.Todo;
import net.minecraft.world.World;
import optifine.BlockPosM;
import optifine.Config;

public class TodoClient extends Todo {

	@Override
	public boolean isSmoothWorld() {
		return Config.isSmoothWorld();
	}

	@Override
	public boolean isCullFacesLeaves() {
		return Config.isCullFacesLeaves();
	}

	@Override
	public boolean shouldUseRomanianNotation(int level) {
		return Settings.ROMANIAN_NOTATION.i() == 0 ? level <= 100 : Settings.ROMANIAN_NOTATION.i() == 1;
	}

	@Override
	public boolean isServerSide() {
		return false;
	}

	@Override
	public void clientInit(Datapack datapack) {
		System.out.println("Toggling datapack " + datapack.clientSide);
		if (datapack.clientSide instanceof ClientSideDatapack)
			((ClientSideDatapack) datapack.clientSide).clientInit(new ClientRegistrar(datapack.getRegistrar()));
	}

}
