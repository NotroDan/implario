package net.minecraft.command;

import net.minecraft.command.server.*;
import net.minecraft.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ServerCommandManager extends CommandHandler implements IAdminCommand {

	public ServerCommandManager() {
		registerCommand(new CommandTime());
		registerCommand(new CommandGameMode());
		registerCommand(new CommandDifficulty());
		registerCommand(new CommandDefaultGameMode());
		registerCommand(new CommandKill());
		registerCommand(new CommandToggleDownfall());
		registerCommand(new CommandWeather());
		registerCommand(new CommandXP());
		registerCommand(new CommandTeleport());
		registerCommand(new CommandGive());
		registerCommand(new CommandReplaceItem());
		registerCommand(new CommandStats());
		registerCommand(new CommandEffect());
		registerCommand(new CommandEnchant());
		registerCommand(new CommandParticle());
		registerCommand(new CommandEmote());
		registerCommand(new CommandShowSeed());
		registerCommand(new CommandFly());
		registerCommand(new CommandHelp());
		registerCommand(new CommandDebug());
		registerCommand(new CommandMessage());
		registerCommand(new CommandBroadcast());
		registerCommand(new CommandSetSpawnpoint());
		registerCommand(new CommandSetDefaultSpawnpoint());
		registerCommand(new CommandGameRule());
		registerCommand(new CommandClearInventory());
		registerCommand(new CommandTestFor());
		registerCommand(new CommandSpreadPlayers());
		registerCommand(new CommandPlaySound());
		registerCommand(new CommandScoreboard());
		registerCommand(new CommandExecuteAt());
		registerCommand(new CommandTrigger());
		registerCommand(new CommandAchievement());
		registerCommand(new CommandSummon());
		registerCommand(new CommandSetBlock());
		registerCommand(new CommandFill());
		registerCommand(new CommandClone());
		registerCommand(new CommandCompare());
		registerCommand(new CommandBlockData());
		registerCommand(new CommandTestForBlock());
		registerCommand(new CommandMessageRaw());
		registerCommand(new CommandWorldBorder());
		registerCommand(new CommandTitle());
		registerCommand(new CommandEntityData());
		registerCommand(new CommandSet());
		registerCommand(new CommandMemory());
		registerCommand(new CommandGC());

		if (MinecraftServer.getServer().isDedicatedServer()) {
			registerCommand(new CommandDatapack());
			registerCommand(new CommandOp());
			registerCommand(new CommandDeOp());
			registerCommand(new CommandStop());
			registerCommand(new CommandSaveAll());
			registerCommand(new CommandSaveOff());
			registerCommand(new CommandSaveOn());
			registerCommand(new CommandBanIp());
			registerCommand(new CommandUnbanIp());
			registerCommand(new CommandBanPlayer());
			registerCommand(new CommandListBans());
			registerCommand(new CommandUnbanPlayer());
			registerCommand(new CommandServerKick());
			registerCommand(new CommandListPlayers());
			registerCommand(new CommandWhitelist());
			registerCommand(new CommandSetPlayerTimeout());
		} else {
			registerCommand(new CommandPublishLocalServer());
		}

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
				if (entityplayer != sender && minecraftserver.getConfigurationManager().canSendCommands(entityplayer.getGameProfile()) && command.canCommandSenderUseCommand(sender)) {
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
