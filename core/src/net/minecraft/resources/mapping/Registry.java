package net.minecraft.resources.mapping;

import lombok.Data;

import java.util.*;

@Data
public abstract class Registry<ID extends Comparable<ID>, T extends Mechanic<ID>> {

	private final Map<ID, List<T>> registered = new HashMap<>();
	private final Map<T, Integer> active = new HashMap<>();

	public void register(T mechanic) {
		List<T> list = registered.computeIfAbsent(mechanic.getDescriptor(), s -> new ArrayList<>());
		list.add(mechanic);
	}

	public boolean unregister(T entry) {
		List<T> list = registered.get(entry.getDescriptor());
		if (list != null) return list.remove(entry);
		return false;
	}

	public void rebuild() {
		active.clear();
		int id = 0;

		// У каждой механики в реестре есть название - по нему производится сортировка и выдача ID
		List<Map.Entry<ID, List<T>>> values = new ArrayList<>(registered.entrySet());
		values.sort(Map.Entry.comparingByKey());


		for (Map.Entry<ID, List<T>> entry : values) {
			List<T> list = entry.getValue();
			if (list.isEmpty()) continue;

			// Сбрасываем ID всем, у кого он мог остаться
			for (T t : list) t.setId(-1);

			// Сортировка по приоритету механик, самая крутая становится активной
			Collections.sort(list);
			id++;
			T topmost = list.get(0);
			topmost.setId(id);
			active.put(topmost, id);
		}
	}

	public int idFor(T entry) {
		return active.get(entry);
	}

}
