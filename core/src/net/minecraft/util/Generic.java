package net.minecraft.util;

import lombok.Getter;
import net.minecraft.resources.mapping.Mechanic;
import net.minecraft.resources.mapping.Registry;

@Getter
public class Generic<D extends Comparable<D>, T extends Mechanic<D>> {

	private final String name;
	private final Class<T> type;
	private final Registry<D, T> registry;

	public Generic(String name, Class<T> type) {
		this.name = name;
		this.type = type;
		this.registry = new Registry<D, T>() {};
	}



}
