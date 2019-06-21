package vanilla.client.game;

import net.minecraft.client.resources.ClientRegistrar;
import net.minecraft.client.resources.ClientSideLoadable;
import vanilla.ingame.ModuleBossStatus;

public class VanillaIngameModules implements ClientSideLoadable {


	@Override
	public void load(ClientRegistrar registrar) {
		registrar.registerIngameModule("boss_status", new ModuleBossStatus());
	}

}
