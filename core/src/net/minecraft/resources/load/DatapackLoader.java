package net.minecraft.resources.load;

import lombok.Getter;
import net.minecraft.resources.Datapack;
import net.minecraft.util.FileUtil;
import net.minecraft.util.Tree;

import java.io.IOException;
import java.io.InputStream;

public abstract class DatapackLoader extends Tree.Leaf {
	@Getter
	private final long loadedAt = System.currentTimeMillis();

	@Getter
	protected DatapackInfo properties;

	protected Datapack datapack;

	public InputStream getResource(String name) {
		return DatapackLoader.class.getResourceAsStream(name);
	}

	public abstract void close();

	public Datapack getInstance() {
		return datapack;
	}

	@Override
	public String toString() {
		return properties == null ? "dp-" + loadedAt : properties.toString();
	}

	public abstract DatapackInfo prepareReader() throws DatapackLoadException;

	public abstract Datapack createInstance() throws DatapackLoadException;

	public abstract String getName();

	public abstract Class<?> getLocalClass(String name);
}
