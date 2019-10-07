package net.minecraft.resources.mapping;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;

public class MappingCommand implements Mapping {
	private final ICommand command;

	public MappingCommand(ICommand command) {
		this.command = command;
	}

	@Override
	public void apply() {
		CommandHandler.registerCommand(command);
	}

	@Override
	public void revert() {
		CommandHandler.unregisterCommand(command);
	}
}
