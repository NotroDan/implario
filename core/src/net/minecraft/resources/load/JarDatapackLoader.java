package net.minecraft.resources.load;

import lombok.Getter;
import net.minecraft.logging.Log;
import net.minecraft.resources.Datapack;
import net.minecraft.server.Todo;
import net.minecraft.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class JarDatapackLoader extends DatapackLoader {

	@Getter
	private final File jarFile;

	private DatapackClassLoader loader;

	public JarDatapackLoader(File jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public String toString() {
		return jarFile.getName();
	}

	@Override
	public DatapackInfo prepareReader() throws DatapackLoadException {
		try {
			loader = new DatapackClassLoader(jarFile, System.class.getClassLoader());
		} catch (IOException ex) {
			throw new DatapackLoadException(ex);
		}

		Log.MAIN.info("Preparing " + jarFile.getAbsolutePath());

		byte[] read = read("datapack.yml");
		if (read == null) throw new DatapackLoadException(null, "Can't read datapack.yml");
		String yml = new String(read);
		String[] lines = yml.replace("\t", "").replace("\r", "").split("\n");
		Map<String, String> config = new HashMap<>();
		String current = null;
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("#") || line.isEmpty()) continue;
			String[] split = line.split(": ", 2);
			if (split.length == 1 && current != null) {
				config.put(current, config.get(current) + "\n" + split[0]);
			} else if (split.length == 2) {
				current = split[0].toLowerCase();
				config.put(current, split[1]);
			}
		}

		String domain = config.get("domain").toLowerCase();
		String serverMain = config.get("server-class");
		String clientMain = config.get("client-class");
		String repo = config.get("repo");
		String releasePrefix = config.get("release-prefix");
		String description = config.get("description");
		String dep = config.get("depend");
		String[] dependencies = dep == null ? new String[0] : dep.split(", ");

		return properties = new DatapackInfo(domain, serverMain, clientMain, dependencies, repo, releasePrefix, description);

	}


	private byte[] read(String name) {
		InputStream in = getResource(name);
		try {
			byte array[] = new byte[in.available()];
			FileUtil.readInputStream(in, array);
			return array;
		} catch (IOException ex) {
			return null;
		}
	}

	@Override
	public String getName() {
		return jarFile.getName();
	}

	@Override
	public Datapack createInstance() throws DatapackLoadException {
		if (datapack != null) return datapack;
		String serverClass = properties.getServerMain();
		String clientClass = properties.getClientMain();
		try {
			Log.MAIN.debug("JarDPL '" + getName() + "' is loading its server-side stuff from '" + serverClass + "'");

			Class mainClass = loadClass(serverClass);
			this.datapack = (Datapack) mainClass.getConstructors()[0].newInstance();

			if (clientClass == null || Todo.instance.isServerSide()) return datapack;
			Log.MAIN.debug("JarDPL '" + getName() + "' also has client-side stuff. Initializing '" + clientClass + "'");
			Class client = loadClass(clientClass);
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
