package net.minecraft.command.api;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.handling.args.ArgsParser;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.BlockPos;

import java.util.Collection;

public interface Arg<T> {

	String getCaption();

	String getDescription();

	Arg<T> defaults(OptionalArgFiller<T> filler);

	Arg<T> defaults(T defaultValue);

	Arg<T> tabCompleter(TabCompleter tabCompleter);

	T getDefaultValue(ArgsParser parser);

	T get(ArgsParser parser);

	Collection<String> performTabCompletion(MPlayer player, BlockPos pos);

	boolean isEssential();

	int getEssentialPartsAmount();

}
