package vanilla;

import net.minecraft.client.gui.ingame.Modules;
import vanilla.ingame.ModuleBossStatus;

public class VanillaIngameModules {


	public static void register() {
		Modules.register(
				new ModuleBossStatus()
						);
	}

}
