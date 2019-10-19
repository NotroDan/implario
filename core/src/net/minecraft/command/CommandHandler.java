package net.minecraft.command;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.logging.Log;
import net.minecraft.util.BlockPos;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.Logger;

public class CommandHandler implements ICommandManager {

	private static final Logger logger = Logger.getInstance();
	private static final Map<String, ICommand> commandMap = Maps.newHashMap();

	@Override
	public int executeCommand(ICommandSender sender, String rawCommand) {
		rawCommand = rawCommand.trim();

		if (rawCommand.startsWith("/"))
			rawCommand = rawCommand.substring(1);

		String[] astring = rawCommand.split(" ");
		String s = astring[0];
		astring = dropFirstString(astring);
		ICommand icommand = commandMap.get(s);

		int i = getUsernameIndex(icommand, astring);
		int j = 0;

		if (icommand == null) {
			ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.notFound");
			chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.sendMessage(chatcomponenttranslation);
		} else if (icommand.canCommandSenderUseCommand(sender)) {
			if (i > -1) {
				List<Entity> list = PlayerSelector.matchEntities(sender, astring[i], Entity.class);
				String s1 = astring[i];
				sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());

				for (Entity entity : list) {
					astring[i] = entity.getUniqueID().toString();

					if (tryExecute(sender, astring, icommand, rawCommand)) {
						++j;
					}
				}

				astring[i] = s1;
			} else {
				sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);

				if (tryExecute(sender, astring, icommand, rawCommand)) {
					++j;
				}
			}
		} else {
			ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
			chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.sendMessage(chatcomponenttranslation1);
		}

		sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, j);
		return j;
	}

	public static int executeClientSide(ICommandSender sender, String rawCommand) {
		String[] astring = rawCommand.split(" ");
		String s = astring[0];
		astring = dropFirstString(astring);
		ICommand icommand = commandMap.get(s);

		if(icommand == null)return -1;
		if(icommand.clientProcessSupported()){
			try{
				icommand.processClientCommand(sender, astring);
			}catch (Exception ex){
				ex.printStackTrace();
				sender.sendMessage("ошибко привет");
			}
		}
		return 0;
	}

	protected static boolean tryExecute(ICommandSender sender, String[] args, ICommand command, String input) {
		try {
			command.processCommand(sender, args);
			return true;
		} catch (WrongUsageException wrongusageexception) {
			ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.generic.usage",
					new Object[] {new ChatComponentTranslation(wrongusageexception.getMessage(), wrongusageexception.getErrorObjects())});
			chatcomponenttranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.sendMessage(chatcomponenttranslation2);
		} catch (CommandException commandexception) {
			ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(commandexception.getMessage(), commandexception.getErrorObjects());
			chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.sendMessage(chatcomponenttranslation1);
		} catch (Throwable var9) {
			Log.DEBUG.exception(var9);
			ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.exception", new Object[0]);
			chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.sendMessage(chatcomponenttranslation);
			logger.warn("Couldn\'t process command: \'" + input + "\'");
		}

		return false;
	}

	public static ICommand registerCommand(ICommand command) {
		commandMap.put(command.getCommandName(), command);

		for (String s : command.getCommandAliases()) {
			ICommand icommand = commandMap.get(s);

			if (icommand == null || !icommand.getCommandName().equals(s))
				commandMap.put(s, command);
		}

		return command;
	}

	public static void unregisterCommand(ICommand command) {
		commandMap.remove(command.getCommandName());
		for (String commands : command.getCommandAliases()) {
			ICommand icommand = commandMap.get(commands);

			if (icommand != null && icommand.getCommandName().equals(command.getCommandName()))
				commandMap.remove(commands);
		}
	}

	public static ICommand getCommand(String command) {
		return commandMap.get(command);
	}

	/**
	 * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
	 */
	private static String[] dropFirstString(String[] input) {
		String[] astring = new String[input.length - 1];
		System.arraycopy(input, 1, astring, 0, input.length - 1);
		return astring;
	}

	public List<String> getTabCompletionOptions(ICommandSender sender, String input, BlockPos pos) {
		String[] astring = input.split(" ", -1);
		String s = astring[0];

		if (astring.length == 1) {
			List<String> list = new ArrayList<>();

			for (Entry<String, ICommand> entry : this.commandMap.entrySet()) {
				if (CommandBase.doesStringStartWith(s, (String) entry.getKey()) && ((ICommand) entry.getValue()).canCommandSenderUseCommand(sender)) {
					list.add(entry.getKey());
				}
			}

			return list;
		}
		if (astring.length > 1) {
			ICommand icommand = (ICommand) this.commandMap.get(s);

			if (icommand != null && icommand.canCommandSenderUseCommand(sender)) {
				return icommand.addTabCompletionOptions(sender, dropFirstString(astring), pos);
			}
		}

		return null;
	}

	public List<ICommand> getPossibleCommands(ICommandSender sender) {
		List<ICommand> list = new ArrayList<>();
		for (ICommand icommand : commandMap.values())
			if (icommand.canCommandSenderUseCommand(sender))
				list.add(icommand);

		return list;
	}

	public Map<String, ICommand> getCommands() {
		return commandMap;
	}

	/**
	 * Return a command's first parameter index containing a valid username.
	 */
	private static int getUsernameIndex(ICommand command, String[] args) {
		if (command == null) {
			return -1;
		}
		for (int i = 0; i < args.length; ++i) {
			if (command.isUsernameIndex(args, i) && PlayerSelector.matchesMultiplePlayers(args[i])) {
				return i;
			}
		}

		return -1;
	}

}
