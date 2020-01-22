package net.minecraft.command.impl.server;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.command.impl.core.SyntaxErrorException;
import net.minecraft.command.impl.core.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.functional.StringUtils;

public class CommandUnbanIp extends CommandBase {
	@Override
	public String getCommandName() {
		return "unban-ip";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("unban-ip");
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isLanServer() && super.canCommandSenderUseCommand(sender);
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.unbanip.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 1) {
			Matcher matcher = CommandBanIp.field_147211_a.matcher(args[0]);

			if (matcher.matches()) {
				MinecraftServer.getServer().getConfigurationManager().getBannedIPs().removeEntry(args[0]);
				notifyOperators(sender, this, "commands.unbanip.success", new Object[] {args[0]});
			} else {
				throw new SyntaxErrorException("commands.unbanip.invalid", new Object[0]);
			}
		} else {
			throw new WrongUsageException("commands.unbanip.usage", new Object[0]);
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? StringUtils.filterCompletions(args, MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getKeys()) : null;
	}

}
