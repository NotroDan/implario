package net.minecraft.block.properties;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.util.EnumFacing;

public class PropertyDirection extends PropertyEnum<EnumFacing> {

	protected PropertyDirection(String name, Collection<EnumFacing> values) {
		super(name, EnumFacing.class, values);
	}

	/**
	 * Create a new PropertyDirection with the given name
	 */
	public static PropertyDirection create(String name) {
		return create(name, (__) -> true);
	}

	/**
	 * Create a new PropertyDirection with all directions that match the given Predicate
	 */
	public static PropertyDirection create(String name, Predicate<EnumFacing> filter) {
		return create(name, Arrays.stream(EnumFacing.values()).filter(filter).collect(Collectors.toList()));
	}

	/**
	 * Create a new PropertyDirection for the given direction values
	 */
	public static PropertyDirection create(String name, Collection<EnumFacing> values) {
		return new PropertyDirection(name, values);
	}

}
