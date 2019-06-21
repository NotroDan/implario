package net.minecraft.resources.mapping;

public class MappingLambda<T> extends Mapping<T> {

	private final Mapper mapper;

	public MappingLambda(int id, String address, T old, T neo, Mapper<T> mapper) {
		super(address, old, neo);
		this.mapper = mapper;
	}

	@Override
	public void map(T element) {
		mapper.override(address, element);
	}

	@FunctionalInterface
	public interface Mapper<T> {

		void override(String address, T element);

	}

}
