package net.minecraft.command;

import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.BlockPos;

public interface ICommandManager {

	int executeCommand(Sender sender, String rawCommand);

	Collection<String> getTabCompletionOptions(MPlayer player, String input, BlockPos pos);

	List<ICommand> getPossibleCommands(Sender sender);

}
