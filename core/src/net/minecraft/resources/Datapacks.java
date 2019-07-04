package net.minecraft.resources;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.logging.Log;
import net.minecraft.resources.load.DatapackLoadException;
import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.resources.load.JarDatapackLoader;
import net.minecraft.resources.load.SimpleDatapackLoader;
import net.minecraft.server.Todo;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Datapacks {

	@Getter
	private final List<DatapackLoader> loaders = new ArrayList<>();

	@Getter
	private final List<Datapack> datapacks = new ArrayList<>();

	public DatapackLoader loadSimple(Datapack datapack) {
		DatapackLoader loader = new SimpleDatapackLoader(datapack);
		loaders.add(loader);
		datapacks.add(datapack);
		return loader;
	}

	public DatapackLoader loadFromJar(File jarFile, String mainClass) {

		DatapackLoader loader = new JarDatapackLoader(jarFile, mainClass);
		loaders.add(loader);

		Datapack datapack;

		try {
			datapack = loader.load();
		} catch (DatapackLoadException ex) {
			Throwable cause = ex.getCause();
			Validate.notNull(cause);
			String message;
			if (cause instanceof ClassCastException) message = "Main class '$' wasn't found in #";
			else if (cause instanceof IllegalAccessException | cause instanceof InstantiationException)
				message = "Main class '$' has redefined default constructor incorrectly.";
			else if (cause instanceof IOException) message = "Jarfile # couldn't be opened.";
			else message = "Unknown error ocurred. Main class: '$', jarfile: #";

			throw new RuntimeException(message.replace("#", jarFile.getAbsolutePath()).replace("$", mainClass), ex);
		}

		datapacks.add(datapack);
		return loader;

	}

	public void initSingleDatapack(Datapack datapack) {

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

	public void shutdown() {
		for (DatapackLoader loader : loaders) shutdown(loader);
		datapacks.clear();
		loaders.clear();
	}

	public void shutdown(DatapackLoader loader) {
		Datapack datapack = loader.get();
		if (datapack == null) {
			Log.MAIN.warn(loader + " hadn't loaded anything but still is in the list.");
			return;
		}
		datapack.unload();
		datapack.disable();
		loader.close();
	}

	public void toggle(File jarFile, String mainClass) {
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