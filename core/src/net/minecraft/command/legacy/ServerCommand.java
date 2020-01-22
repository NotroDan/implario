package net.minecraft.command.legacy;

import net.minecraft.command.api.ICommandSender;

public class ServerCommand {

	/**
	 * The command string.
	 */
	public final String command;
	public final ICommandSender sender;

	public ServerCommand(String input, ICommandSender sender) {
		this.command = input;
		this.sender = sender;
	}

}
