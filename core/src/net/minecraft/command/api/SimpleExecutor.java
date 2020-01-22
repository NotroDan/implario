package net.minecraft.command.api;

@FunctionalInterface
public interface SimpleExecutor {

	int execute(ICommandSender sender, String[] args);

}
