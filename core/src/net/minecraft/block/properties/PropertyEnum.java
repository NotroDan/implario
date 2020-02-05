package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.util.IStringSerializable;

public class PropertyEnum<T extends Enum<T> & IStringSerializable> extends PropertyHelper<T> {

	private final ImmutableSet<T> allowedValues;
	private final Map<String, T> nameToValue = new HashMap<>();

	protected PropertyEnum(String name, Class<T> valueClass, Collection<T> allowedValues) {
		super(name, valueClass);
		this.allowedValues = ImmutableSet.copyOf(allowedValues);

		for (T t : allowedValues) {
			String s = ((IStringSerializable) t).getName();

			if (this.nameToValue.containsKey(s)) {
				throw new IllegalArgumentException("Multiple values have the same name \'" + s + "\'");
			}

			this.nameToValue.put(s, t);
		}
	}

	public Collection<T> getAllowedValues() {
		return this.allowedValues;
	}

	/**
	 * Get the name for the given value.
	 */
	public String getName(T value) {
		return ((IStringSerializable) value).getName();
	}

	public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz) {
		return create(name, clazz, (__) -> true);
	}

	public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz, Predicate<T> filter) {
		return create(name, clazz, Arrays.stream(clazz.getEnumConstants()).filter(filter).collect(Collectors.toList()));
	}

	@SafeVarargs
	public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz, T... values) {
		return create(name, clazz, Arrays.asList(values));
	}

	public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz, Collection<T> values) {
		return new PropertyEnum<>(name, clazz, values);
	}

}
