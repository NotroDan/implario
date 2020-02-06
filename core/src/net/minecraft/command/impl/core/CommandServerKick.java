package net.minecraft.command.impl.core;

import java.util.List;

import net.minecraft.command.api.ICommandSender;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.functional.StringUtils;

public class CommandServerKick extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "kick";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 3;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.kick.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 0 && args[0].length() > 1) {
			MPlayer entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(args[0]);
			String s = "Kicked by an operator.";
			boolean flag = false;

			if (entityplayermp == null) {
				throw new PlayerNotFoundException();
			}
			if (args.length >= 2) {
				s = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
				flag = true;
			}

			entityplayermp.playerNetServerHandler.kickPlayerFromServer(s);

			if (flag) {
				notifyOperators(sender, this, "commands.kick.success.reason", new Object[] {entityplayermp.getName(), s});
			} else {
				notifyOperators(sender, this, "commands.kick.success", new Object[] {entityplayermp.getName()});
			}
		} else {
			throw new WrongUsageException("commands.kick.usage", new Object[0]);
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length >= 1 ? StringUtils.filterCompletions(args, MinecraftServer.getServer().getAllUsernames()) : null;
	}

}