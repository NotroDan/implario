package net.minecraft.command.server;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.functional.StringUtils;

public class CommandWhitelist extends CommandBase {
	@Override
	public String getCommandName() {
		return "whitelist";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.whitelist.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1)
			throw new WrongUsageException("commands.whitelist.usage");
		MinecraftServer minecraftserver = MinecraftServer.getServer();

		switch (args[0]){
			case "on":
				minecraftserver.getConfigurationManager().setWhiteListEnabled(true);
				notifyOperators(sender, this, "commands.whitelist.enabled");
				return;
			case "off":
				minecraftserver.getConfigurationManager().setWhiteListEnabled(false);
				notifyOperators(sender, this, "commands.whitelist.disabled");
				return;
			case "list":
				sender.sendMessage(new ChatComponentTranslation("commands.whitelist.list",
						minecraftserver.getConfigurationManager().getWhitelistedPlayerNames().size(),
						minecraftserver.getConfigurationManager().getAvailablePlayerDat().length
				));
				sender.sendMessage(new ChatComponentText(joinNiceString(
						minecraftserver.getConfigurationManager().getWhitelistedPlayerNames()
				)));
				return;
			case "add":
				if (args.length < 2)
					throw new WrongUsageException("commands.whitelist.add.usage");
				minecraftserver.getConfigurationManager().addWhitelistedPlayer(args[1]);
				notifyOperators(sender, this, "commands.whitelist.add.success", args[1]);
				return;
			case "remove":
				if (args.length < 2)
					throw new WrongUsageException("commands.whitelist.remove.usage");
				if (!minecraftserver.getConfigurationManager().getWhitelistedPlayers().contains(args[1]))
					throw new CommandException("commands.whitelist.remove.failed", args[1]);
				minecraftserver.getConfigurationManager().removePlayerFromWhitelist(args[1]);
				notifyOperators(sender, this, "commands.whitelist.remove.success", args[1]);
				return;
			case "reload":
				minecraftserver.getConfigurationManager().loadWhiteList();
				notifyOperators(sender, this, "commands.whitelist.reloaded");
				return;
			default:
				throw new WrongUsageException("commands.whitelist.usage");
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1)
			return StringUtils.filterCompletions(args, "on", "off", "list", "add", "remove", "reload");
		if (args.length == 2)
			if (args[0].equals("remove"))
				return StringUtils.filterCompletions(args, MinecraftServer.getServer().getConfigurationManager().getWhitelistedPlayerNames());
			if (args[0].equals("add"))
				return StringUtils.filterCompletions(args, MinecraftServer.getServer().getPlayerProfileCache().getUsernames());
		return null;
	}
}
