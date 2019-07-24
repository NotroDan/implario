package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "save-all";
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.save.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		MinecraftServer minecraftserver = MinecraftServer.getServer();
		sender.sendMessage(new ChatComponentTranslation("commands.save.start"));

		if (minecraftserver.getConfigurationManager() != null) {
			minecraftserver.getConfigurationManager().saveAllPlayerData();
		}

		try {
			for (WorldServer world : minecraftserver.getWorlds()) {
				if (world == null) continue;
				boolean flag = world.disableLevelSaving;
				world.disableLevelSaving = false;
				world.saveAllChunks(true, null);
				world.disableLevelSaving = flag;

			}
			if (args.length > 0 && "flush".equals(args[0])) {
				sender.sendMessage(new ChatComponentTranslation("commands.save.flushStart"));

				for (WorldServer world : minecraftserver.getWorlds()) {
					if (world == null) continue;
					boolean flag1 = world.disableLevelSaving;
					world.disableLevelSaving = false;
					world.saveChunkData();
					world.disableLevelSaving = flag1;
				}
				sender.sendMessage(new ChatComponentTranslation("commands.save.flushEnd"));
			}
		} catch (MinecraftException minecraftexception) {
			notifyOperators(sender, this, "commands.save.failed", minecraftexception.getMessage());
			return;
		}

		notifyOperators(sender, this, "commands.save.success");
	}

}
