package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.chat.ChatComponentTranslation;

public class CommandWhitelist extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "whitelist";
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
		return "commands.whitelist.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new WrongUsageException("commands.whitelist.usage", new Object[0]);
		}
		MinecraftServer minecraftserver = MinecraftServer.getServer();

		if (args[0].equals("on")) {
			minecraftserver.getConfigurationManager().setWhiteListEnabled(true);
			notifyOperators(sender, this, "commands.whitelist.enabled", new Object[0]);
		} else if (args[0].equals("off")) {
			minecraftserver.getConfigurationManager().setWhiteListEnabled(false);
			notifyOperators(sender, this, "commands.whitelist.disabled", new Object[0]);
		} else if (args[0].equals("list")) {
			sender.sendMessage(new ChatComponentTranslation("commands.whitelist.list", new Object[] {
					minecraftserver.getConfigurationManager().getWhitelistedPlayerNames().size(),
					minecraftserver.getConfigurationManager().getAvailablePlayerDat().length
			}));
			List<String> list= minecraftserver.getConfigurationManager().getWhitelistedPlayerNames();
			sender.sendMessage(new ChatComponentText(joinNiceString(list)));
		} else if (args[0].equals("add")) {
			if (args.length < 2) {
				throw new WrongUsageException("commands.whitelist.add.usage", new Object[0]);
			}

			GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[1]);

			if (gameprofile == null) {
				throw new CommandException("commands.whitelist.add.failed", new Object[] {args[1]});
			}

			minecraftserver.getConfigurationManager().addWhitelistedPlayer(gameprofile.getName());
			notifyOperators(sender, this, "commands.whitelist.add.success", new Object[] {args[1]});
		} else if (args[0].equals("remove")) {
			if (args.length < 2)
				throw new WrongUsageException("commands.whitelist.remove.usage", new Object[0]);

			if (!minecraftserver.getConfigurationManager().getWhitelistedPlayers().contains(args[1]))
				throw new CommandException("commands.whitelist.remove.failed", new Object[] {args[1]});

			minecraftserver.getConfigurationManager().removePlayerFromWhitelist(args[1]);
			notifyOperators(sender, this, "commands.whitelist.remove.success", new Object[] {args[1]});
		} else if (args[0].equals("reload")) {
			minecraftserver.getConfigurationManager().loadWhiteList();
			notifyOperators(sender, this, "commands.whitelist.reloaded", new Object[0]);
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, new String[] {"on", "off", "list", "add", "remove", "reload"});
		}
		if (args.length == 2) {
			if (args[0].equals("remove")) {
				return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getWhitelistedPlayerNames());
			}

			if (args[0].equals("add")) {
				return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getPlayerProfileCache().getUsernames());
			}
		}

		return null;
	}

}
