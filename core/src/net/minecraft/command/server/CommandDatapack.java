package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.resources.Datapacks;
import net.minecraft.resources.load.DatapackLoader;

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
			for(DatapackLoader loader : Datapacks.getLoaders())
				if(loader.getName().equals(args[1])){
					byte[] array = loader.getInstance().saveState();
					byte[] playerInfo = Datapacks.removePlayerInfo(loader.getInstance());
					Datapacks.shutdown(loader);
					try{
						Datapacks.accept(loader);
						Datapacks.initSingleDatapack(loader.getInstance());
						if(array != null)loader.getInstance().loadState(array);
						if(playerInfo != null)Datapacks.loadPlayerInfo(loader.getInstance(), playerInfo);
						sender.sendMessage("все норм");
					}catch (Exception loadException){
						loadException.printStackTrace();
						sender.sendMessage("ошыбка");
						sender.sendMessage(loadException.getMessage());
						return;
					}
					return;
				}
			Datapacks.getLoaders().get(0).getName();
			Datapacks.shutdown();
			Datapacks.fullInitializeDatapacks(new File("datapacks"));
		}
	}
}
