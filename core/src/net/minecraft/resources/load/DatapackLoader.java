package net.minecraft.resources.load;

import lombok.Getter;
import net.minecraft.resources.Datapack;
import net.minecraft.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;

public abstract class DatapackLoader {

	@Getter
	private final long loadedAt = System.currentTimeMillis();

	protected Datapack datapack;

	public abstract Datapack load(String main, String clientMain) throws DatapackLoadException;

	public InputStream getResource(String name) {
		return DatapackLoader.class.getResourceAsStream(name);
	}

	public abstract void close();

	public Datapack get() {
		return datapack;
	}

	public void init() throws DatapackLoadException {}

	public byte[] read(String name) {
		InputStream in = getResource(name);
		try {
			byte array[] = new byte[in.available()];
			FileUtil.readInputStream(in, array);
			return array;
		} catch (IOException ex) {
			return null;
		}
	}

	public abstract String getName();

}
