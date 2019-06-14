package net.minecraft.command;

import net.minecraft.resources.Datapack;
import net.minecraft.resources.DatapackReflector;
import net.minecraft.util.chat.ChatComponentText;

public class CommandDatapack extends CommandBase {

	@Override
	public String getCommandName() {
		return "dp";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "dp [Class]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

		if (args.length == 0) throw new WrongUsageException("Использование: /dp [Class]");
		try {
			Datapack datapack = DatapackReflector.enable(args[0]);
			sender.addChatMessage(new ChatComponentText("§aДатапак §f" + datapack.getDomain().getAddress() + "§a подключён и работает (§fSERVER-SIDE§a)."));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
