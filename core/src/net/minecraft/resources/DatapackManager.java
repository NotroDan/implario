package net.minecraft.resources;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.logging.Log;
import net.minecraft.resources.load.DatapackInfo;
import net.minecraft.resources.load.DatapackLoadException;
import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.resources.load.SimpleDatapackLoader;
import net.minecraft.util.Tree;

import java.util.*;

@UtilityClass
public class DatapackManager {

	public final DatapackLoader CORE = new SimpleDatapackLoader(null, DatapackInfo.builder().domain("minecraft").build());

	// ToDo: Избавиться от боксинга
	//  После нескольких не слишком настойчивых попыток найти реализацию мапы int -> Object было решено отложить решение этого вопроса.
	private final Map<Integer, DatapackLoader> map = new WeakHashMap<>();

	@Getter
	private final Tree<DatapackLoader> tree = new Tree<>(CORE);

	public void prepare(DatapackLoader loader) throws DatapackLoadException {
		DatapackInfo properties = loader.prepareReader();
		map.put(properties.hashCode(), loader);
		for (String dependency : properties.getDependencies()) {
			map.get(dependency.hashCode()).growBranch(loader);
		}
		tree.getRootElement().growBranch(loader);
	}

	public void 

	public void release(DatapackLoader loader) {
		map.remove(loader.getProperties().hashCode());
		try {
			List<DatapackLoader> dependents = tree.resolve(loader);
			for (DatapackLoader dependent : dependents) {
				Log.MAIN.info("Releasing " + loader + "...");
				map.remove(dependent.getProperties().hashCode());
			}
		} catch (Tree.CircularDependencyException e) {
			e.printStackTrace();
		}
	}


	public void resolveDependencies() {
		try {
			tree.resolve();
		} catch (Tree.CircularDependencyException e) {
			// ToDo: Перейти на менее радикальный метод обработки циклических зависимостей
			Log.MAIN.error(e.toString());
			throw new RuntimeException(e);
		}

	}

}
