package net.minecraft.resources;

import net.minecraft.logging.Log;
import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.resources.load.JarDatapackLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Datapacks {

	private static final List<DatapackLoader> loaders = new ArrayList<>();
	private static final List<Datapack> datapacks = new ArrayList<>();

	public static Collection<DatapackLoader> getLoaders() {
		return loaders;
	}

	public static Collection<Datapack> getLoadedDatapacks() {
		return datapacks;
	}

	public static void loadFromJar(File jarFile, String mainClass) {

		DatapackLoader loader = new JarDatapackLoader(jarFile, mainClass);
		loaders.add(loader);

		Datapack datapack;

		try {
			datapack = loader.load();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Main class '" + mainClass + "' wasn't found in " + jarFile, e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Main class '" + mainClass + "' doesn't have a public constructor.", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("Main class '" + mainClass + "' doesn't have an empty constructor.", e);
		} catch (IOException e) {
			throw new RuntimeException("Jarfile " + jarFile + " couldn't be opened", e);
		} catch (Exception e) {
			throw new RuntimeException("Unknown error", e);
		}

		datapacks.add(datapack);

	}

	public static void shutdown() {
		for (DatapackLoader loader : loaders) {
			Datapack datapack = loader.get();
			if (datapack == null) {
				Log.MAIN.warn(loader + " hadn't loaded anything but still is in the list.");
				continue;
			}
			datapack.unload();
			datapack.disable();
			loader.close();
		}
		datapacks.clear();
		loaders.clear();
	}

}
