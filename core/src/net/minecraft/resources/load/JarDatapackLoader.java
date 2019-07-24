package net.minecraft.resources.load;

import lombok.Getter;
import net.minecraft.resources.Datapack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class JarDatapackLoader extends DatapackLoader {

	@Getter
	private final File jarFile;

	private DatapackClassLoader loader;

	public JarDatapackLoader(File jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public void init() throws DatapackLoadException {
		try {
			loader = new DatapackClassLoader(jarFile, System.class.getClassLoader());
		} catch (IOException ex) {
			throw new DatapackLoadException(ex);
		}
	}

	@Override
	public Datapack load(String main) throws DatapackLoadException {
		try {
			if (datapack != null) return datapack;

			Class mainClass = loadClass(main);

			return (datapack = (Datapack) mainClass.getConstructors()[0].newInstance());
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
			throw new DatapackLoadException(ex);
		}
	}

	@Override
	public InputStream getResource(String name) {
		return loader.getResourceAsStream(name);
	}

	private Class<?> loadClass(String name) {
		try {
			return loader.loadClass(name);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void close() {
		loader.close();
		datapack = null;
	}

}
