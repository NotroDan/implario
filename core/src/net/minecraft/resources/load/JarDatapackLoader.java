package net.minecraft.resources.load;

import lombok.Getter;
import net.minecraft.logging.Log;
import net.minecraft.resources.Datapack;
import net.minecraft.server.Todo;

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
	public Datapack load(String main, String clientMain) throws DatapackLoadException {
		try {
			if (datapack != null) return datapack;
			Log.DEBUG.info("Loading main class " + main);

			Class mainClass = loadClass(main);
			this.datapack = (Datapack) mainClass.getConstructors()[0].newInstance();
			Log.DEBUG.info("clientMain: " + clientMain);

			if (clientMain == null || Todo.instance.isServerSide()) return datapack;
			Class client = loadClass(clientMain);
			datapack.clientSide = client.getConstructors()[0].newInstance();
			return datapack;
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
