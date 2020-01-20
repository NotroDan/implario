package net.minecraft.command.api;

import net.minecraft.command.ICommandSender;

@FunctionalInterface
public interface SimpleExecutor {

	int execute(ICommandSender sender, String[] args);

}
