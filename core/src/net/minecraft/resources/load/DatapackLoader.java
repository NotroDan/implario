package net.minecraft.resources.load;

import net.minecraft.resources.Datapack;

public abstract class DatapackLoader {

	protected Datapack datapack;

	public abstract Datapack load() throws DatapackLoadException;

	public abstract void close();

	public Datapack get() {
		return datapack;
	}

}
