package net.minecraft.command.impl.server;

import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandStop extends CommandBase {
	@Override
	public String getCommandName() {
		return "stop";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.stop.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (MinecraftServer.getServer().worldService != null) notifyOperators(sender, this, "commands.stop.start");
		MinecraftServer.getServer().initiateShutdown();
	}
}
