package net.minecraft.command;

import lombok.Data;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.functional.ArrayUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
public class ComplexCommand implements ICommand {
	private final Map<String, ICommand> subcommands = new HashMap<>();

	private final String description;
	private final String name;
	private final String permissionLadder;
	private final int permissionLevel;

	public ComplexCommand(String name, String description, String ladder, int permissionLevel) {
		this.name = name;
		this.description = description;
		this.permissionLadder = ladder;
		this.permissionLevel = permissionLevel;
	}

	@Override
	public int execute(Sender sender, String[] args) {
		ICommand subcommand = subcommands.get(args.length == 0 ? "" : args[0].toLowerCase());
		if (subcommand == null) {
			sender.sendMessage("§cИспользование: §f/" + getName() + " §7[§f" + String.join("§7|§f", subcommands.keySet()) + "§7]");
			return 0;
		}
		args = ArrayUtils.dropFirstArg(args);
		return subcommand.execute(sender, args);
	}

	@Override
	public Collection<String> tabComplete(MPlayer player, BlockPos pos, String[] args) {
		if (args.length == 1) return subcommands.keySet();
		ICommand subcommand = subcommands.get(args.length == 0 ? "" : args[0].toLowerCase());
		return subcommand.tabComplete(player, pos, ArrayUtils.dropFirstArg(args));
	}
}
