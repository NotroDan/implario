package net.minecraft.resources.load;

import lombok.Getter;
import net.minecraft.resources.Datapack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JarDatapackLoader extends DatapackLoader {

	@Getter
	private final String mainClassName;

	@Getter
	private final File jarFile;

	private DatapackClassLoader loader;

	public JarDatapackLoader(File jarFile, String mainClass) {
		this.jarFile = jarFile;
		this.mainClassName = mainClass;
	}

	public Datapack load() throws DatapackLoadException {
		try {
			if (datapack != null) return datapack;

			loader = new DatapackClassLoader(jarFile, System.class.getClassLoader());
			Class<? extends Datapack> mainClass = (Class<? extends Datapack>) loadClass(mainClassName);

			datapack = mainClass.newInstance();
			return datapack;
		} catch (IOException | InstantiationException | IllegalAccessException ex) {
			throw new DatapackLoadException(ex);
		}
	}

	public Datapack getDatapack() {
		return datapack;
	}

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