package net.minecraft.client.gui.ingame;

import java.util.ArrayList;
import java.util.List;

public class Modules {

	static final List<Module> MODULES = new ArrayList<>();

	static {
		register(
				new ModuleVignette(),
				new ModulePumpkin(),
				new ModulePortal(),
				new ModuleItemInfo()
				);
	}

	public static void register(Module module) {
		MODULES.add(module);
	}
	public static void register(Module... modules) {
		for (Module module : modules) register(module);
	}

}
