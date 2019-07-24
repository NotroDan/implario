package net.minecraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CyclicIterator<T> implements Iterator<T> {

	private Object[] array;
	private int pos = 0;

	public CyclicIterator(T[] array) {
		this.array = array;
	}

	public CyclicIterator(Collection<T> collection) {
		array = collection.toArray();
	}

	public static <T> CyclicIterator<T> empty() {
		return new CyclicIterator<>(new ArrayList<>());
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@SuppressWarnings ("uncheked")
	@Override
	public T next() {
		if (array.length <= ++pos) pos = 0;
		return (T) array[pos];
	}

	public int size() {
		return array.length;
	}

	@SuppressWarnings ("uncheked")
	public T current() {
		return (T) array[pos];
	}

	public void add(T t) {
		Object[] a = new Object[array.length + 1];
		System.arraycopy(array, 0, a, 0, array.length);
		a[array.length] = t;
		array = a;
	}

}
