package net.minecraft.resources;

import java.util.function.Function;

public class Provider<Input, Output> {

	private Function<Input, Output> function;

	public Provider(Function<Input, Output> defaultFunction) {
		this.function = defaultFunction;
	}

	public Output provide(Input input) {
		return function.apply(input);
	}

	public Function<Input, Output> getFunction() {
		return function;
	}

	public void setFunction(Function<Input, Output> function) {
		this.function = function;
	}

}
