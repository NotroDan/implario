package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.resources.Datapacks;

import java.io.File;

public class CommandDatapack extends CommandBase {

	@Override
	public String getCommandName() {
		return "dp";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "dp [Jar]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) throw new WrongUsageException("Использование: /dp [Jar]");
		String cmd = args[0];
		if(cmd.equals("reload")){
			Datapacks.shutdown();
			Datapacks.fullInitializeDatapacks(new File("datapacks"));
		}
	}
}
