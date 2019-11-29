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
import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.byteable.FastDecoder;
import net.minecraft.util.byteable.FastEncoder;

import java.io.File;
import java.util.*;

@UtilityClass
public class DatapackManager {

	public static final String MINECRAFT = "minecraft";
	public final DatapackLoader ROOT = new SimpleDatapackLoader(new Datapack(MINECRAFT) {}, DatapackInfo.builder().domain("minecraft").build());

	private final Map<String, DatapackLoader> map = new HashMap<>();

	@Getter
	private final Tree<DatapackLoader> tree = new Tree<>(ROOT);

	private final Set<Datapack> datapacks = new HashSet<>();

	public void prepare(DatapackLoader loader) throws DatapackLoadException {
		DatapackInfo properties = loader.prepareReader();
		map.put(properties.getDomain(), loader);
		for (String dependency : properties.getDependencies()) {
			map.get(dependency).growBranch(loader);
		}
		tree.getRootElement().growBranch(loader);
	}

	public DatapackLoader importJar(File jar) throws DatapackLoadException {
		DatapackLoader loader = new JarDatapackLoader(jar);
		prepare(loader);
		tree.rebuild();
		return loader;
	}

	public void importDir(File dir) throws DatapackLoadException {
		if (!dir.isDirectory()) return;
		for (File file : dir.listFiles()) {
			if (file.isDirectory() || !file.getAbsolutePath().endsWith(".jar")) continue;
			DatapackLoader loader = new JarDatapackLoader(file);
			prepare(loader);
		}
		tree.rebuild();
	}

	public void loadDir(File dir){
		try {
			importDir(dir);
			for (DatapackLoader loader : DatapackManager.getTree().loadingOrder()) {
				Log.MAIN.info("Instantiating datapack" + loader);
				try {
					DatapackManager.load(loader);
				}catch (DatapackLoadException ex){
					ex.printStackTrace();
				}
			}
		} catch (DatapackLoadException e) {
			e.printStackTrace();
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

	public void init(DatapackLoader loader) {
		loader.getInstance().init();
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
}
