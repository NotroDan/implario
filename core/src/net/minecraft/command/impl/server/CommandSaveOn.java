package net.minecraft.command.impl.server;

import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandSaveOn extends CommandBase {
	@Override
	public String getCommandName() {
		return "save-on";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.save-on.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		MinecraftServer minecraftserver = MinecraftServer.getServer();
		boolean flag = false;

		for (WorldServer world : minecraftserver.getWorlds()) {
			if (world == null) continue;
			if (world.disableLevelSaving) {
				world.disableLevelSaving = false;
				flag = true;
			}
		}

		if (flag) notifyOperators(sender, this, "commands.save.enabled");
		else throw new CommandException("commands.save-on.alreadyOn");
	}
}
