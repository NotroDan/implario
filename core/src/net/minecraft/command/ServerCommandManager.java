package net.minecraft.command;

import net.minecraft.command.server.*;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class  ServerCommandManager extends CommandHandler implements IAdminCommand {

	public ServerCommandManager() {
		CommandBase.setAdminCommander(this);
	}

	/**
	 * Send an informative message to the server operators
	 */
	public void notifyOperators(ICommandSender sender, ICommand command, int flags, String msgFormat, Object... msgParams) {
		boolean flag = true;
		MinecraftServer minecraftserver = MinecraftServer.getServer();

		if (!sender.sendCommandFeedback()) flag = false;

		IChatComponent ichatcomponent = new ChatComponentTranslation("chat.type.admin", sender.getName(), new ChatComponentTranslation(msgFormat, msgParams));
		ichatcomponent.getChatStyle().setColor(EnumChatFormatting.GRAY);
		ichatcomponent.getChatStyle().setItalic(Boolean.TRUE);

		if (flag) {
			for (Player entityplayer : minecraftserver.getConfigurationManager().getPlayers()) {
				if (entityplayer != sender && minecraftserver.getConfigurationManager().canSendCommands((MPlayer)entityplayer) && command.canCommandSenderUseCommand(sender)) {
					boolean flag1 = sender instanceof MinecraftServer && MinecraftServer.getServer().opsSeeConsole();

					if (flag1 || !(sender instanceof MinecraftServer)) {
						entityplayer.sendMessage(ichatcomponent);
					}
				}
			}
		}

		if (sender != minecraftserver && minecraftserver.getEntityWorld().getGameRules().getBoolean("logAdminCommands")) {
			minecraftserver.sendMessage(ichatcomponent);
		}

		boolean flag3 = minecraftserver.getEntityWorld().getGameRules().getBoolean("sendCommandFeedback");

		if (sender instanceof CommandBlockLogic) {
			flag3 = ((CommandBlockLogic) sender).shouldTrackOutput();
		}

		if ((flags & 1) != 1 && flag3 || sender instanceof MinecraftServer)
			sender.sendMessage(new ChatComponentTranslation(msgFormat, msgParams));
	}

}
