package net.minecraft.resources.mapping;

import net.minecraft.command.ICommand;
import net.minecraft.command.CommandRegistry;

public class MappingCommand implements Mapping {

	private final ICommand command;

	public MappingCommand(ICommand command) {
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
