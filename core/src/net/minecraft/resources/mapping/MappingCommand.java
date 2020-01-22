package net.minecraft.resources.mapping;

import net.minecraft.command.api.Command;
import net.minecraft.command.api.CommandRegistry;

public class MappingCommand implements Mapping {

	private final Command command;

	public MappingCommand(Command command) {
		this.command = command;
	}

	@Override
	public void apply() {
		CommandRegistry.registerCommand(command);
	}

	@Override
	public void revert() {
		CommandRegistry.unregisterCommand(command);
	}

}
