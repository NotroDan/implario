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
	public void close() {}

}
