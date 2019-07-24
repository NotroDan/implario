package net.minecraft.resources.load;

import net.minecraft.resources.Datapack;

import java.io.IOException;
import java.io.InputStream;

public abstract class DatapackLoader {
	protected Datapack datapack;

	public abstract Datapack load(String main) throws DatapackLoadException;

	public InputStream getResource(String name){
		return DatapackLoader.class.getResourceAsStream(name);
	}

	public abstract void close();

	public Datapack get() {
		return datapack;
	}

	public void init() throws DatapackLoadException{}

	public byte[] read(String name){
		InputStream in = getResource(name);
		try{
			byte array[] = new byte[in.available()];
			in.read(array);
			in.close();
			return array;
		}catch (IOException ex){
			return null;
		}
	}
}
