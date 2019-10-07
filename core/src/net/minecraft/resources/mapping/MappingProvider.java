package net.minecraft.resources.mapping;

import net.minecraft.resources.Provider;

import java.util.function.Function;

public class MappingProvider<I, O> extends AbstractMapping<Function<I, O>> {

	private final Provider provider;

	public MappingProvider(Provider provider, Function<I, O> function) {
		super("provider", provider.getFunction(), function);
		this.provider = provider;
	}

	@Override
	public void map(Function<I, O> element) {
		provider.setFunction(element);
	}

}
