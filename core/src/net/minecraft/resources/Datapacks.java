package net.minecraft.resources;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.player.Module;
import net.minecraft.entity.player.ModuleManager;
import net.minecraft.entity.player.Player;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.logging.Log;
import net.minecraft.resources.load.DatapackLoadException;
import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.resources.load.JarDatapackLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Todo;
import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.byteable.FastDecoder;
import net.minecraft.util.byteable.FastEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Datapacks {

	@Getter
	private final List<DatapackLoader> loaders = new ArrayList<>();

	@Getter
	private final List<Datapack> datapacks = new ArrayList<>();

	public Datapack accept(DatapackLoader loader) throws DatapackLoadException {
		Datapack datapack;

		loader.prepareReader();
		datapack = loader.createInstance();

		loaders.add(loader);
		datapacks.add(datapack);
		return datapack;
	}

	public void shutdown(DatapackLoader loader) {
		Datapack datapack = loader.getInstance();
		if (datapack == null) {
			Log.MAIN.warn(loader + " hadn't loaded anything but still is in the list.");
			return;
		}
		datapacks.remove(datapack);
		datapack.unload();
		datapack.disable();
		loader.close();
	}
}
