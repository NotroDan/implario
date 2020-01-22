package net.minecraft.command.impl.core;

import net.minecraft.command.api.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.functional.StringUtils;
import net.minecraft.world.WorldServer;

import java.util.List;

public class CommandTime extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "time";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 2;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.time.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 1) {
			if (args[0].equals("set")) {
				int l;

				switch (args[1]) {
					case "day":
						l = 1000;
						break;
					case "night":
						l = 13000;
						break;
					default:
						l = parseInt(args[1], 0);
						break;
				}

				this.setTime(l);
				notifyOperators(sender, this, "commands.time.set", l);
				return;
			}

			if (args[0].equals("add")) {
				int k = parseInt(args[1], 0);
				this.addTime(k);
				notifyOperators(sender, this, "commands.time.added", k);
				return;
			}

			if (args[0].equals("query")) {
				if (args[1].equals("daytime")) {
					int j = (int) (sender.getEntityWorld().getWorldTime() % 2147483647L);
					sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, j);
					notifyOperators(sender, this, "commands.time.query", j);
					return;
				}

				if (args[1].equals("gametime")) {
					int i = (int) (sender.getEntityWorld().getTotalWorldTime() % 2147483647L);
					sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, i);
					notifyOperators(sender, this, "commands.time.query", i);
					return;
				}
			}
		}

		throw new WrongUsageException("commands.time.usage");
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? StringUtils.filterCompletions(args, "set", "add", "query") : args.length == 2 && args[0].equals("set") ? StringUtils.filterCompletions(args, "day",
				"night") : args.length == 2 && args[0].equals("query") ? StringUtils.filterCompletions(args,
				"daytime", "gametime") : null;
	}

	/**
	 * Set the time in the server object.
	 */
	protected void setTime(int time) {
		for (WorldServer world : MinecraftServer.getServer().getWorlds()) {
			world.setWorldTime(time);
		}
	}

	/**
	 * Adds (or removes) time in the server object.
	 */
	protected void addTime(int time) {
		for (WorldServer world : MinecraftServer.getServer().getWorlds()) {
			world.setWorldTime(world.getWorldTime() + time);
		}
	}

}
