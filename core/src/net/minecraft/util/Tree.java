package net.minecraft.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Tree.Leaf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class Tree<T extends Leaf> {

	@Getter
	private final T rootElement;

	private List<T> lastUnloadResolution;
	private List<T> lastLoadResolution;

	@SuppressWarnings("unchecked")
	public Tree<T> rebuild() {
		List<T> list = new ArrayList<>();
		rootElement.recursiveResolve((List<Leaf>) list, new ArrayList<>());
		lastUnloadResolution = list;
		lastLoadResolution = new ArrayList<>(list);
		Collections.reverse(lastLoadResolution);
		return this;
	}

	@SuppressWarnings ("unchecked")
	public List<T> buildUnloadingFrom(Leaf leaf) {
		List<T> list = new ArrayList<>();
		leaf.recursiveResolve((List<Leaf>) list, new ArrayList<>());
		return list;
	}

	public List<T> buildLoadingFrom(Leaf leaf) {
		List<T> list = buildUnloadingFrom(leaf);
		Collections.reverse(list);
		return list;
	}

	public List<T> loadingOrder() {
		return lastLoadResolution;
	}

	public List<T> unloadingOrder() {
		return lastUnloadResolution;
	}

	@Data
	public static class Leaf {
		private final List<Leaf> dependents = new ArrayList<>();

		public void growBranch(Leaf leaf) {
			dependents.add(leaf);
		}

		List<Leaf> recursiveResolve(List<Leaf> cache, List<Leaf> hanged) {
			hanged.add(this);
			for (Leaf branch : getDependents()) {
				if (cache.contains(branch)) continue;
				if (hanged.contains(branch)) continue; // Циклическая зависимость
				branch.recursiveResolve(cache, hanged);
			}
			cache.add(this);
			hanged.remove(this);
			return cache;
		}

	}
	
}
