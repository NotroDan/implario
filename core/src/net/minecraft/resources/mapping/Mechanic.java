package net.minecraft.resources.mapping;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Mechanic<ID> implements Comparable<Mechanic> {

	private final ID descriptor;
	private int priority;

	@EqualsAndHashCode.Exclude
	private int id;

	@Override
	public int compareTo(Mechanic other) {
		return this.priority - other.priority;
	}

	public void init() {

	}

}
