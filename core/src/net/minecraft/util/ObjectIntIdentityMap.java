package net.minecraft.util;

import com.google.common.collect.Iterators;

import java.util.*;

public class ObjectIntIdentityMap<T> implements IObjectIntIterable {

	private final IdentityHashMap<T, Object> identityMap = new IdentityHashMap<>(512);
	private final List<T> objectList = new ArrayList<>();

	public void put(T key, int value) {
		this.identityMap.put(key, value);

		while (this.objectList.size() <= value) {
			this.objectList.add(null);
		}

		this.objectList.set(value, key);
	}

	public void remove(T key) {
		int value = get(key);
		this.identityMap.remove(key);
		if (value != -1) this.objectList.set(value, null);
	}

	public int get(T key) {
		Integer integer = (Integer) this.identityMap.get(key);
		return integer == null ? -1 : integer;
	}

	public final T getByValue(int value) {
		return value >= 0 && value < this.objectList.size() ? this.objectList.get(value) : null;
	}

	public Iterator iterator() {
		return Iterators.filter(this.objectList.iterator(), Objects::nonNull);
	}

	public List<T> getObjectList() {
		return this.objectList;
	}

}
