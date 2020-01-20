package net.minecraft.command.api;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.entity.player.Player;
import net.minecraft.util.BlockPos;

import java.util.Collection;

@FunctionalInterface
public interface TabCompleter {

	Collection<String> tabComplete(MPlayer player, BlockPos pos);

}
