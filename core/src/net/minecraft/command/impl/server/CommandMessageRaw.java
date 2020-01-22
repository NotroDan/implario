package net.minecraft.command.impl.server;

import com.google.gson.JsonParseException;

import java.util.List;

import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.command.impl.core.SyntaxErrorException;
import net.minecraft.command.impl.core.WrongUsageException;
import net.minecraft.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.chat.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.functional.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class CommandMessageRaw extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "tellraw";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 2;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.tellraw.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2)
			throw new WrongUsageException("commands.tellraw.usage");
		Player entityplayer = getPlayer(sender, args[0]);
		String s = buildString(args, 1);

		try {
			IChatComponent ichatcomponent = IChatComponent.Serializer.jsonToComponent(s);
			entityplayer.sendMessage(ChatComponentProcessor.processComponent(sender, ichatcomponent, entityplayer));
		} catch (JsonParseException jsonparseexception) {
			Throwable throwable = ExceptionUtils.getRootCause(jsonparseexception);
			throw new SyntaxErrorException("commands.tellraw.jsonException", new Object[] {throwable == null ? "" : throwable.getMessage()});
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? StringUtils.filterCompletions(args, MinecraftServer.getServer().getAllUsernames()) : null;
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}

}
