package net.minecraft.resources;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.Module;
import net.minecraft.entity.player.ModuleManager;
import net.minecraft.entity.player.Player;
import net.minecraft.logging.Log;
import net.minecraft.resources.load.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tree;
import oogle.util.byteable.*;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;

@UtilityClass
public class DatapackManager {


	public final DatapackLoader ROOT = new SimpleDatapackLoader(new DatapackMinecraft() {}, DatapackInfo.builder().domain(DatapackMinecraft.MINECRAFT).build());

	private final Map<String, DatapackLoader> map = new HashMap<>();

	@Getter
	private final Tree<DatapackLoader> tree = new Tree<>(ROOT);

	private final Set<Datapack> datapacks = new HashSet<>();

	public void prepare(DatapackLoader loader) {
		DatapackInfo properties;
		try {
			properties = loader.prepareReader();
		} catch (DatapackLoadException ex) {
			System.out.println("Unable to load datapack " + loader);
			return;
		}
		map.put(properties.getDomain(), loader);
		if(properties.getDependencies() != null)
			for (String dependency : properties.getDependencies())
				map.get(dependency).growBranch(loader);
		tree.getRootElement().growBranch(loader);
	}

	public void prepare(Iterable<DatapackLoader> loaders) {
		tree.rebuild();
		for (DatapackLoader loader : loaders) prepare(loader);
		tree.rebuild();
	}

	public void prepareAndLoad(Iterable<DatapackLoader> loaders){
		prepare(loaders);
		for (DatapackLoader loader : DatapackManager.getTree().loadingOrder()) {
			Log.MAIN.info("Instantiating datapack" + loader);
			try {
				DatapackManager.load(loader);
			} catch (DatapackLoadException ex) {
				ex.printStackTrace();
			}
		}
		initializeModules();
	}

	public DatapackLoader validateJar(String path) {
		return validateJar(new File(path));
	}
	public DatapackLoader validateJar(File jar) {
		if (!jar.exists() || !jar.isFile() || !jar.getName().endsWith(".jar")) return null;
		return new JarDatapackLoader(jar);
	}

	public List<DatapackLoader> validateDir(File dir) {
		List<DatapackLoader> loaders = new ArrayList<>();
		loaders.add(ROOT);
		if (!dir.isDirectory()) return loaders;

		for (File file : dir.listFiles()) {
			DatapackLoader loader = validateJar(file);
			if (loader != null) loaders.add(loader);
		}
		return loaders;
	}

	public void prepareDir(File dir) {
		prepare(validateDir(dir));
		tree.rebuild();
	}

	public void prepareAndLoadDir(File dir) {
		prepareDir(dir);
		for (DatapackLoader loader : DatapackManager.getTree().loadingOrder()) {
			Log.MAIN.info("Instantiating datapack" + loader);
			try {
				DatapackManager.load(loader);
			} catch (DatapackLoadException ex) {
				ex.printStackTrace();
			}
		}
		initializeModules();
	}

	public Datapack load(DatapackLoader loader) throws DatapackLoadException {
		Datapack datapack = loader.createInstance();
		datapacks.add(datapack);
		return datapack;
	}

	public DatapackLoader getLoaderByName(String name) {
		return map.get(name);
	}

	public Datapack getDatapack(String name){
		DatapackLoader loader = getLoaderByName(name);
		return loader == null ? null : loader.getInstance();
	}

	public void shutdownBranch(DatapackLoader loader) {
		List<DatapackLoader> dependents = tree.buildUnloadingFrom(loader);
		for (DatapackLoader dependent : dependents) {
			if (dependent == loader) continue;
			Log.MAIN.info("Releasing " + dependent + "...");
			shutdownBranch(dependent);
		}
		map.remove(loader.getProperties().getDomain());
		loader.getInstance().unload();
		loader.getInstance().disable();
		loader.close();
	}

	public void loadBranch(DatapackLoader loader) throws DatapackLoadException{
		List<DatapackLoader> load = tree.buildLoadingFrom(loader);
		prepare(loader);
		for (DatapackLoader dependent : load) {
			if (dependent == loader) continue;
			Log.MAIN.info("Loading " + dependent + "...");
			loadBranch(dependent);
		}
		loader.createInstance();
		loader.getInstance().preinit();
		loader.getInstance().init();
	}

	public Iterable<DatapackLoader> getLoaders(){
		return map.values();
	}

	public void initializeModules(){
		List<String> modules = new ArrayList<>();
		for(Datapack datapack : datapacks)
			if(datapack.moduleManager() != null){
				datapack.moduleManager().writeID(modules.size());
				modules.add(datapack.getDomain());
			}
		DatapackManager.modules = modules.toArray(new String[]{});
	}

	public int getModulesSize(){
		return modules.length;
	}

	public String getDatapackByModuleID(int id){
		return modules[id];
	}

	private String[] modules;

	public static byte[] removePlayerInfo(Datapack datapack) {
		if (MinecraftServer.mcServer == null) return null;
		ModuleManager manager = datapack.moduleManager();
		if (manager == null) return null;
		String domain = manager.getDomain();
		BytesEncoder encoder = new FastEncoder();
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
				encoder.writeString(player.getName());
				if (manager.supportedWorld())
					encodeSecure(() -> manager.encodeWorld(module), encoder, domain);
				if (manager.supportedGlobal())
					encodeSecure(() -> manager.encodeGlobal(module), encoder, domain);
				if (manager.supportedMemory())
					encodeSecure(() -> manager.encodeMemory(module), encoder, domain);
			} catch (Throwable throwable) {
				Log.MAIN.error("Error on write nbt data, domain " + datapack.getDomain() + " module manager " + module.manager(), throwable);
			}
			manager.clearModule(player);
		}
		return encoder.generate();
	}

	private static void encodeSecure(Supplier<byte[]> supplier, Encoder encoder, String domain){
		byte array[] = null;
		try{
			array = supplier.get();
		}catch (Throwable error){
			Log.MAIN.error("Error on write nbt data domain: " + domain, error);
		}
		encoder.writeBoolean(array != null);
		if(array != null)encoder.writeBytes(array);
	}

	public static void loadPlayerInfo(Datapack datapack, byte array[]) {
		if (MinecraftServer.mcServer == null) return;
		ModuleManager manager = datapack.moduleManager();
		if (manager == null) {
			Log.MAIN.warn("ModuleManager on datapack " + datapack.moduleManager() + " not found, but nbt data founded");
			return;
		}
		Decoder decoder = new FastDecoder(array);
		int size = decoder.readInt();
		String domain = manager.getDomain();
		for (int i = 0; i < size; i++) {
			try {
				String player = decoder.readStr();
				Module module = manager.createEmptyModule();
				if (manager.supportedWorld())
					decodeSecure(() -> manager.decodeWorld(module, decoder.readBytes()), decoder, domain);
				if (manager.supportedGlobal())
					decodeSecure(() -> manager.decodeGlobal(module, decoder.readBytes()), decoder, domain);
				if (manager.supportedMemory())
					decodeSecure(() -> manager.decodeMemory(module, decoder.readBytes()), decoder, domain);
				Player mplayer = MinecraftServer.mcServer.getConfigurationManager().getPlayerByUsername(player);
				if (mplayer == null) continue;
				manager.setModule(mplayer, module);
			} catch (Throwable throwable) {
				Log.MAIN.error("Error on read nbt data, domain " + datapack.getDomain() + " module manager " + datapack.moduleManager(), throwable);
			}
		}
	}

	private static void decodeSecure(Runnable runnable, Decoder decoder, String domain){
		if(!decoder.readBoolean())return;
		try{
			runnable.run();
		}catch (Throwable error){
			Log.MAIN.error("Error on write nbt data domain: " + domain, error);
		}
	}
}
