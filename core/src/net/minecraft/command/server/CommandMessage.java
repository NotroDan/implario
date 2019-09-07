package net.minecraft.command.server;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.chat.ChatStyle;

public class CommandMessage extends CommandBase {
	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList("w", "msg");
	}

	@Override
	public String getCommandName() {
		return "tell";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.message.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2)
			throw new WrongUsageException("commands.message.usage");
		Player entityplayer = getPlayer(sender, args[0]);

		if (entityplayer == sender)
			throw new PlayerNotFoundException("commands.message.sameTarget");
		IChatComponent ichatcomponent = getChatComponentFromNthArg(sender, args, 1, !(sender instanceof Player));
		ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.message.display.incoming",
				sender.getDisplayName(), ichatcomponent);
		ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.message.display.outgoing",
				entityplayer.getDisplayName(), ichatcomponent);
		chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.GRAY);
		chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.GRAY);
		entityplayer.sendMessage(chatcomponenttranslation);
		sender.sendMessage(chatcomponenttranslation1);
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}
}
