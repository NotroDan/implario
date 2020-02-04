package net.minecraft.command.legacy;

import net.minecraft.command.Sender;

public class ServerCommand {

	/**
	 * The command string.
	 */
	public final String command;
	public final Sender sender;

	public ServerCommand(String input, Sender sender) {
		this.command = input;
		this.sender = sender;
	}

}
