package net.minecraft.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Tree.Leaf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class Tree<T extends Leaf> implements Iterable<T> {

	@Getter
	private final T rootElement;

	private List<T> lastResolution;

	@Override
	public Iterator<T> iterator() {
		if (lastResolution == null) throw new IllegalStateException("Tried to iterate over unresolved tree");
		return lastResolution.iterator();
	}

	public List<T> resolve() throws CircularDependencyException {
		return lastResolution = resolve(rootElement);
	}

	@SuppressWarnings ("unchecked")
	public List<T> resolve(T from) throws CircularDependencyException {
		List<T> list = new ArrayList<>();
		from.recursiveResolve((List<Leaf>) list, new ArrayList<>());
		return list;
	}

	@Data
	public static class Leaf {
		private final List<Leaf> dependents = new ArrayList<>();

		public void growBranch(Leaf leaf) {
			dependents.add(leaf);
		}

		List<Leaf> recursiveResolve(List<Leaf> cache, List<Leaf> hanged) throws CircularDependencyException {
			hanged.add(this);
			for (Leaf branch : getDependents()) {
				if (cache.contains(branch)) continue;
				if (hanged.contains(branch)) throw new CircularDependencyException(hanged.toArray());
				branch.recursiveResolve(cache, hanged);
			}
			cache.add(this);
			hanged.remove(this);
			return cache;
		}
	}
	
	@Data
	@EqualsAndHashCode(callSuper = false)
	public static class CircularDependencyException extends Exception {
		private final Object[] members;
	}

}
