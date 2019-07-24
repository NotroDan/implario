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
import net.minecraft.server.Todo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Datapacks {

	@Getter
	private final List<DatapackLoader> loaders = new ArrayList<>();

	@Getter
	private final List<Datapack> datapacks = new ArrayList<>();

	public Datapack load(DatapackLoader loader) {
		return load(loader, null);
	}

	public Datapack load(DatapackLoader loader, String name) {
		loaders.add(loader);

		Datapack datapack;

		try {
			loader.init();
			datapack = loader.load(name == null ?
					new String(loader.read("datapack.resource"), StandardCharsets.UTF_8) : name);
		} catch (DatapackLoadException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}

		datapacks.add(datapack);
		return datapack;
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

}
