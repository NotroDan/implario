package net.minecraft.client.resources;

import net.minecraft.client.gui.ingame.Module;
import net.minecraft.client.gui.ingame.Modules;
import net.minecraft.resources.mapping.Mapping;

public class MappingIngameModule extends Mapping<Module> {

	public MappingIngameModule(String name, Module existing, Module replacement) {
		super(name, existing, replacement);
	}

	@Override
	public void map(Module element) {
		if (element == null) Modules.unregister(address);
		else Modules.register(address, element);
	}

}
