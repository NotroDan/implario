package net.minecraft.resources.mapping;

import lombok.Data;

import java.util.*;

@Data
public abstract class Registry<T extends Entry> {

	private final List<T> entries = new ArrayList<>();
	private final Map<T, Integer> mapping = new HashMap<>();

	public void register(T entry) {
		entries.add(entry);
	}

	public void unregister(T entry) {
		entries.remove(entry);
	}

	public void rebuild() {
		mapping.clear();
		Collections.sort(entries);
		for (int id = 0; id < entries.size(); id++) {
			T entry = entries.get(id);
			entry.setId(id);
			mapping.put(entry, id);
		}
	}

	public int idFor(T entry) {
		return mapping.get(entry);
	}

}
