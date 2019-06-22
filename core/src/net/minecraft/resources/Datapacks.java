package net.minecraft.resources;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.logging.Log;
import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.resources.load.JarDatapackLoader;
import net.minecraft.server.Todo;

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

	public static DatapackLoader loadFromJar(File jarFile, String mainClass) {

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
		return loader;

	}

	public static void initSingleDatapack(Datapack datapack) {

		datapack.loadBlocks();
		Block.reloadBlockStates();
		Blocks.reload();

		BlockFire.init();

		Item.registerItems();
		datapack.loadItems();
		Items.reload();

		datapack.preinit();
		Blocks.reload();
		Block.reloadBlockStates();
		datapack.init();
		Todo.instance.clientInit(datapack);
		datapack.ready();

	}

	public static void shutdown() {
		for (DatapackLoader loader : loaders) shutdown(loader);
		datapacks.clear();
		loaders.clear();
	}

	public static void shutdown(DatapackLoader loader) {
		Datapack datapack = loader.get();
		if (datapack == null) {
			Log.MAIN.warn(loader + " hadn't loaded anything but still is in the list.");
			return;
		}
		datapack.unload();
		datapack.disable();
		loader.close();
	}

	public static void toggle(File jarFile, String mainClass) {
		DatapackLoader enabled = null;
		for (DatapackLoader loader : loaders) {
			if (loader instanceof JarDatapackLoader && ((JarDatapackLoader) loader).getMainClassName().equals(mainClass)) {
				enabled = loader;
				break;
			}
		}
		if (enabled == null) {
			DatapackLoader datapackLoader = loadFromJar(jarFile, mainClass);
			initSingleDatapack(datapackLoader.get());
		}
		else {
			shutdown(enabled);
			loaders.remove(enabled);
		}

	}

}
