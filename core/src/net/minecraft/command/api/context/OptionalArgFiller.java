package net.minecraft.command.api.context;

@FunctionalInterface
public interface OptionalArgFiller<T> {

	T fill(ArgsParser parser);

}
