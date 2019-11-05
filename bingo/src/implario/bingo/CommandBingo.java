package implario.bingo;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandBingo extends CommandBase {

	@Override
	public String getCommandName() {
		return "bingo";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "bingo [start]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {

	}

}
