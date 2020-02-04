package net.minecraft.command;

import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.BlockPos;

import java.util.Collection;
import java.util.function.Function;

public interface Arg<T> {

	String getCaption();

	String getDescription();

	Arg<T> defaults(Function<ArgsParser, T> filler);

	Arg<T> defaults(T defaultValue);

	Arg<T> tabCompleter(TabCompleter tabCompleter);

	T getDefaultValue(ArgsParser parser);

	T get(ArgsParser parser);

	Collection<String> performTabCompletion(MPlayer player, BlockPos pos, String arg);

	boolean isEssential();

	int getEssentialPartsAmount();

	@FunctionalInterface
	interface TabCompleter {
		Collection<String> tabComplete(MPlayer player, BlockPos pos);
	}

}
