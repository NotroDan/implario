package net.minecraft.command.api;

import lombok.Getter;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.BlockPos;

import java.util.Collection;

@Getter
public class SimpleCommand implements Command {

	private final String description;
	private final String address;
	private final String permissionLadder;
	private final int permissionLevel;
	private final SimpleExecutor executor;
	private Completer completer;

	public SimpleCommand(String address, String description, String permissionLadder, int permissionLevel, SimpleExecutor executor) {
		this.address = address;
		this.description = description;
		this.permissionLadder = permissionLadder;
		this.permissionLevel = permissionLevel;
		this.executor = executor;
	}

	public SimpleCommand completer(Completer completer) {
		this.completer = completer;
		return this;
	}

	public int execute(ICommandSender sender, String[] args) {
		return executor.execute(sender, args);
	}

	@Override
	public Collection<String> tabComplete(MPlayer player, BlockPos pos, String[] args) {
		if (completer != null) return completer.complete(player, pos, args);
		else return null;
	}

	@FunctionalInterface
	public interface Completer {
		Collection<String> complete(MPlayer player, BlockPos pos, String[] args);
	}

}

