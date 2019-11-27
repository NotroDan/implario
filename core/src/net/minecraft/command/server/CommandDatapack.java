package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.resources.DatapackManager;
import net.minecraft.resources.Datapacks;
import net.minecraft.resources.load.DatapackLoader;

import javax.xml.crypto.Data;
import java.io.File;

public class CommandDatapack extends CommandBase {

	@Override
	public String getCommandName() {
		return "dp";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "dp reload [Jar]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) throw new WrongUsageException("Использование: /dp reload [Jar]");

		String cmd = args[0];
		if(cmd.equals("reload")){
			for(DatapackLoader loader : DatapackManager.getLoaders()) {
				sender.sendMessage(loader.getProperties().getDomain());
				if (loader.getProperties().getDomain().equalsIgnoreCase(args[1])) {
					byte[] array = loader.getInstance().saveState();
					byte[] playerInfo = DatapackManager.removePlayerInfo(loader.getInstance());
					try {
						DatapackManager.loadBranch(loader);
						if (array != null) loader.getInstance().loadState(array);
						if (playerInfo != null) DatapackManager.loadPlayerInfo(loader.getInstance(), playerInfo);
						sender.sendMessage("все норм");
					} catch (Exception loadException) {
						loadException.printStackTrace();
						sender.sendMessage("ошыбка");
						sender.sendMessage(loadException.getMessage() + "");
						return;
					}
					return;
				}
			}
		}
	}
}
