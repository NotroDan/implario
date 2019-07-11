package net.minecraft.resources.event;

import org.apache.commons.lang3.ArrayUtils;

public class EventManager<T extends Event<T>> {

	private Listener<T>[] array;

	public void add(Listener<T> listener) {
		Listener<T>[] array = new Listener[this.array == null ? 1 : this.array.length + 1];
		if (this.array != null) System.arraycopy(this.array, 0, array, 0, this.array.length);
		this.array = array;
		array[array.length - 1] = listener;
	}

	public void remove(Listener<T> listener) {
		if (array == null) return;
		int index = -1;
		for (int i = 0; i < array.length; i++) if (array[i] == listener) index = i;
		if (index == -1) return;
		array = ArrayUtils.remove(array, index);
	}

	public void call(T event) {
//		System.out.println(Thread.currentThread().getName() + " | " + event);
		if (array == null) return;
		for (Listener<T> listener : array) listener.process(event);
	}

	public boolean isUseful() {
		return array != null && array.length != 0;
	}

}
