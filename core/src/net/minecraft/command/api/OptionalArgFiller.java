package net.minecraft.command.api;

import net.minecraft.command.handling.args.ArgsParser;

@FunctionalInterface
public interface OptionalArgFiller<T> {

	T fill(ArgsParser parser);

}
