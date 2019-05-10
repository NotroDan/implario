package net.minecraft.resources;

import java.util.HashMap;
import java.util.Map;

public class Additions {

	private final Map<Label<?>, ? super Object> map = new HashMap<>();

	public <T> T get(Label<T> label) {
		return (T) map.get(label);
	}

	public <T> void put(Label<T> label, T value) {
		map.put(label, value);
	}

}
