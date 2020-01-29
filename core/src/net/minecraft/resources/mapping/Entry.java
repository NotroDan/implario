package net.minecraft.resources.mapping;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Entry implements Comparable<Entry> {

	private final String domain;
	private final String name;

	@EqualsAndHashCode.Exclude
	private int id;

	@Override
	public int compareTo(Entry entry) {
		return getDomain().compareTo(entry.getDomain()) * 2 +
				getName().compareTo(entry.getName());
	}

	public void init() {

	}

}
