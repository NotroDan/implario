package net.minecraft.resources.load;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Datapack;

@RequiredArgsConstructor
public class SimpleDatapackLoader extends DatapackLoader {

	@Getter
	private final Datapack datapack;

	@Override
	public Datapack load(String name) throws DatapackLoadException {
		return datapack;
	}

	@Override
	public byte[] read(String name) {
		throw new UnsupportedOperationException("Unable to read resource from simple datapack. Requested resource is '" + name + "'");
	}

	@Override
	public void close() {}

}
