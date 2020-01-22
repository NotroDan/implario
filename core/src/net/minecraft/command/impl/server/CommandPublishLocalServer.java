package net.minecraft.command.impl.server;

import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings;

public class CommandPublishLocalServer extends CommandBase {
	@Override
	public String getCommandName() {
		return "publish";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return MinecraftServer.getServer().isSinglePlayer();
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.publish.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		String s = MinecraftServer.getServer().shareToLAN(WorldSettings.GameType.SURVIVAL, false);

		if (s != null) notifyOperators(sender, this, "commands.publish.started", s);
		else notifyOperators(sender, this, "commands.publish.failed");
	}
}
