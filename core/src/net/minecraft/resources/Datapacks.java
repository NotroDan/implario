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
import net.minecraft.security.MinecraftSecurityManager;
import net.minecraft.server.Todo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Datapacks {

	@Getter
	private final List<DatapackLoader> loaders = new ArrayList<>();

	@Getter
	private final List<Datapack> datapacks = new ArrayList<>();

	public Datapack load(DatapackLoader loader) throws DatapackLoadException{
		return load(loader, null);
	}

	public Datapack load(DatapackLoader loader, String name) throws DatapackLoadException {
		loaders.add(loader);

		Datapack datapack;

		loader.init();
		byte[] read = loader.read("datapack.resource");
		String names = new String(read, StandardCharsets.UTF_8);
		String[] classes = names.split("\\|");
		datapack = loader.load(name == null ? classes[0] : name, classes.length > 1 ? classes[1] : null);

		datapacks.add(datapack);
		return datapack;
	}

	public void initSingleDatapack(Datapack datapack) {

		datapack.loadBlocks();
		Block.reloadBlockStates();
		Blocks.reload();

		BlockFire.init();

		datapack.loadItems();
		Items.reload();

		datapack.preinit();
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

	public static Datapack initializeDatapack(File datapack){
		DatapackLoader loader = new JarDatapackLoader(datapack);
		try{
			return load(loader);
		}catch (DatapackLoadException ex){
			System.out.println("Не удалось загрузить " + datapack.getAbsolutePath());
			ex.printStackTrace();
		}
		return null;
	}

	public static void initializeDatapacks(File directory){
		File files[] = directory.listFiles();
		if(files == null)return;
		for(File file : files){
			if(file.isDirectory() || !file.getName().endsWith(".jar"))continue;
			initializeDatapack(file);
		}
	}
}
