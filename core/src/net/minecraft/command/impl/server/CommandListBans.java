package net.minecraft.command.impl.server;

import java.util.List;

import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.functional.StringUtils;

public class CommandListBans extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "banlist";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 3;
	}

	/**
	 * Returns true if the given command sender is allowed to use this command.
	 */
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return (MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isLanServer() || MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer()) && super.canCommandSenderUseCommand(
				sender);
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.banlist.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 1 && args[0].equalsIgnoreCase("ips")) {
			sender.sendMessage(new ChatComponentTranslation("commands.banlist.ips", new Object[] {MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getKeys().length}));
			sender.sendMessage(new ChatComponentText(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getKeys())));
		} else {
			sender.sendMessage(new ChatComponentTranslation("commands.banlist.players", new Object[] {MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getKeys().length}));
			sender.sendMessage(new ChatComponentText(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getKeys())));
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? StringUtils.filterCompletions(args, new String[] {"players", "ips"}) : null;
	}

}