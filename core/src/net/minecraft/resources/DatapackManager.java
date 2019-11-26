package net.minecraft.resources;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.logging.Log;
import net.minecraft.resources.load.*;
import net.minecraft.util.Tree;

import java.io.File;
import java.util.*;

@UtilityClass
public class DatapackManager {

	public final DatapackLoader ROOT = new SimpleDatapackLoader(new Datapack(Domain.MINECRAFT) {}, DatapackInfo.builder().domain("minecraft").build());

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

	@Deprecated
	public DatapackLoader getLoaderByName(String name) {
		return map.get(name);
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

	public void initializeModules(){
		List<Domain> modules = new ArrayList<>();
		for(Datapack datapack : datapacks)
			if(datapack.moduleManager() != null){
				datapack.moduleManager().writeID(modules.size());
				modules.add(datapack.getDomain());
			}
		DatapackManager.modules = modules.toArray(new Domain[]{});
	}

	public int getModulesSize(){
		return modules.length;
	}

	private Domain[] modules;
}
