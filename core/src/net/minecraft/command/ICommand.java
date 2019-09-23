package net.minecraft.command;

import java.util.List;

import net.minecraft.util.BlockPos;

public interface ICommand extends Comparable<ICommand> {
	String getCommandName();

	String getCommandUsage(ICommandSender sender);

	List<String> getCommandAliases();

	void processCommand(ICommandSender sender, String[] args) throws CommandException;

	boolean canCommandSenderUseCommand(ICommandSender sender);

	List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos);

	default boolean clientProcessSupported(){
		return false;
	}

	default void processClientCommand(ICommandSender sender, String args[]){}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	boolean isUsernameIndex(String[] args, int index);
}
