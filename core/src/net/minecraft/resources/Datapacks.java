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

	}

	public void shutdown() {
		for (DatapackLoader loader : loaders) shutdown(loader);
		datapacks.clear();
		loaders.clear();
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

	public static Datapack acceptJar(File datapack) {
		DatapackLoader loader = new JarDatapackLoader(datapack);
		try {
			return accept(loader);
		} catch (DatapackLoadException ex) {
			Log.MAIN.error("Load failed " + datapack.getAbsolutePath(), ex);
		}
		return null;
	}

	public static void initializeDatapacks(File directory) {
		File files[] = directory.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isDirectory() || !file.getName().endsWith(".jar")) continue;
			acceptJar(file);
		}
	}

	public static void fullInitializeDatapacks(File directory) {
		initializeDatapacks(directory);
		Bootstrap.register();
		for (Datapack datapack : Datapacks.getDatapacks())
			datapack.init();
	}

	public Datapack getDatapack(Domain domain) {
		for (Datapack datapack : datapacks)
			if (datapack.getDomain().equals(domain)) return datapack;
		return null;
	}

	public static byte[] removePlayerInfo(Datapack datapack) {
		if (MinecraftServer.mcServer != null) {
			ModuleManager manager = datapack.moduleManager();
			Encoder encoder = new FastEncoder();
			int players = 0;
			for (Player player : MinecraftServer.mcServer.getConfigurationManager().getPlayers()) {
				Module module = manager.getModule(player);
				if (module == null) continue;
				players++;
			}
			encoder.writeInt(players);
			for (Player player : MinecraftServer.mcServer.getConfigurationManager().getPlayers()) {
				Module module = manager.getModule(player);
				if (module == null) continue;
				try {
					byte world[] = module.manager().encodeWorld(module);
					byte global[] = module.manager().encodeGlobal(module);
					encoder.writeString(player.getName());
					encoder.writeBoolean(world != null);
					if(world != null)encoder.writeBytes(world);
					encoder.writeBoolean(global != null);
					if(world != null)encoder.writeBytes(global);
				} catch (Throwable throwable) {
					Log.MAIN.error("Error on write nbt data, domain " + datapack.getDomain() + " module manager " + module.manager(), throwable);
				}
				manager.clearModule(player);
			}
			return encoder.generate();
		}
		return null;
	}

	public static  void loadPlayerInfo(Datapack datapack, byte array[]) {
		if (MinecraftServer.mcServer == null) return;
		Decoder decoder = new FastDecoder(array);
		int size = decoder.readInt();
		ModuleManager manager = datapack.moduleManager();
		if (manager == null) {
			Log.MAIN.warn("ModuleManager on datapack " + datapack.moduleManager() + " not found, but nbt data founded");
			return;
		}
		for (int i = 0; i < size; i++) {
			try {
				String player = decoder.readStr();
				byte world[] = decoder.readBoolean() ? decoder.readBytes() : null,
					 global[] = decoder.readBoolean() ? decoder.readBytes() : null;
				Module module = datapack.moduleManager().decode(world, global);
				Player mplayer = MinecraftServer.mcServer.getConfigurationManager().getPlayerByUsername(player);
				if (mplayer == null) continue;
				manager.setModule(mplayer, module);
			} catch (Throwable throwable) {
				Log.MAIN.error("Error on read nbt data, domain " + datapack.getDomain() + " module manager " + datapack.moduleManager(), throwable);
			}
		}
	}

}
