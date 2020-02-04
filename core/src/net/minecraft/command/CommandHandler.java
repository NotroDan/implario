package net.minecraft.command;


import lombok.Getter;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.logging.Log;
import net.minecraft.util.BlockPos;
import net.minecraft.util.functional.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.functional.ArrayUtils.*;

@Getter
public class CommandHandler implements ICommandManager {
	@Override
	public int executeCommand(Sender sender, String rawCommand) {
		rawCommand = rawCommand.trim();

		if (rawCommand.startsWith("/"))
			rawCommand = rawCommand.substring(1);

		String[] argsArray = rawCommand.split(" ");
		String commandName = argsArray[0];
		ICommand cmd = CommandRegistry.getCommand(commandName);

		if (cmd == null) {
			sender.sendMessage("§cКоманда §f/" + commandName + "§c не найдена.");
			return 0;
		}
		if (!sender.canCommandSenderUseCommand(cmd.getPermissionLevel(), cmd.getName())) {
			sender.sendMessage("§7/" + commandName + ": §fТребуется " + cmd.getPermissionLevel() + " уровень доступа " + cmd.getPermissionLadder());
			return 0;
		}

		argsArray = dropFirstArg(argsArray);

		try {
			return cmd.execute(sender, argsArray);
		} catch (Throwable t) {
			Log.MAIN.error("При выполнении команды произошла ошибка: " + rawCommand, t);
			sender.sendMessage("§cПри выполнении команды произошла ошибка: " + t.getMessage());
			return 0;
		}

	}

	@Override
	public Collection<String> getTabCompletionOptions(MPlayer player, String input, BlockPos pos) {
		String[] argsArray = input.split(" ", -1);
		String s = argsArray[0];

		if (argsArray.length == 1) {
			List<String> list = new ArrayList<>();

			for (Map.Entry<String, ICommand> entry : CommandRegistry.getCommandMap().entrySet()) {
				ICommand cmd = entry.getValue();
				if (StringUtils.doesStringStartWith(s, entry.getKey()) &&
						player.canCommandSenderUseCommand(cmd.getPermissionLevel(), cmd.getName())) {
					list.add(entry.getKey());
				}
			}

			return list;
		}
		if (argsArray.length > 1) {
			ICommand command = CommandRegistry.getCommand(s);
			if (command == null) return null;

			argsArray = dropFirstArg(argsArray);
			return command.tabComplete(player, pos, argsArray);
		}

		return null;
	}

	@Override
	public List<ICommand> getPossibleCommands(Sender sender) {
		List<ICommand> list = new ArrayList<>();
		for (ICommand cmd : CommandRegistry.getAllCommands())
			if (sender.canCommandSenderUseCommand(cmd.getPermissionLevel(), cmd.getName()))
				list.add(cmd);

		return list;
	}
}

