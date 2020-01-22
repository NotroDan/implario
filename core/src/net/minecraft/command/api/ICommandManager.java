package net.minecraft.command.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.BlockPos;

public interface ICommandManager {

	int executeCommand(ICommandSender sender, String rawCommand);

	Collection<String> getTabCompletionOptions(MPlayer player, String input, BlockPos pos);

	List<Command> getPossibleCommands(ICommandSender sender);

}
