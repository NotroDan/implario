package net.minecraft.command.impl.server;

import com.mojang.authlib.GameProfile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.command.impl.core.WrongUsageException;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.functional.StringUtils;

public class CommandOp extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "op";
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
		return "commands.op.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 0) {
			MinecraftServer minecraftserver = MinecraftServer.getServer();
			MPlayer mPlayer = minecraftserver.getConfigurationManager().getPlayerByUsername(args[0]);

			if (mPlayer == null)
				throw new CommandException("commands.op.failed", args[0]);
			mPlayer.setPlayerPermission(4);
			notifyOperators(sender, this, "commands.op.success", args[0]);
		} else {
			throw new WrongUsageException("commands.op.usage");
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			String s = args[args.length - 1];
			List<String> list = new ArrayList<>();

			for (GameProfile gameprofile : MinecraftServer.getServer().getGameProfiles()) {
				if (!MinecraftServer.getServer().getConfigurationManager().canSendCommands((MPlayer)sender) && StringUtils.doesStringStartWith(s, gameprofile.getName())) {
					list.add(gameprofile.getName());
				}
			}

			return list;
		}
		return null;
	}

}
