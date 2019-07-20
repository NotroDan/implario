package net.minecraft.client.gui.ingame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Modules {

	static final Map<String, Module> MODULES = new HashMap<>();

	static {
		register("vignette", new ModuleVignette());
		register("pumpkin", new ModulePumpkin());
		register("portal", new ModulePortal());
		register("iteminfo", new ModuleItemInfo());
		register("hotbar", new ModuleHotbar());
		register("playerstats", new ModulePlayerStats());
	}

	public static void register(String name, Module module) {
		MODULES.put(name, module);
	}

	public static Collection<Module> getModules() {
		return MODULES.values();
	}

	public static Module getModule(String id) {
		return MODULES.get(id);
	}

	public static Set<Map.Entry<String, Module>> getEntries() {
		return MODULES.entrySet();
	}

	public static void unregister(String address) {
		MODULES.remove(address);
	}

}
