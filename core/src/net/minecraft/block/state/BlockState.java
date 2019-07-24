package net.minecraft.block.state;

import com.google.common.base.Objects;
import com.google.common.collect.*;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.Cartesian;
import net.minecraft.util.MapPopulator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockState {

	private static final Function<IProperty, String> GET_NAME_FUNC = s -> s == null ? "<NULL>" : s.getName();
	private final Block block;
	private final ImmutableList<IProperty> properties;
	private final ImmutableList<IBlockState> validStates;

	public BlockState(Block b, IProperty... properties) {
		block = b;
		Arrays.sort(properties, Comparator.comparing(IProperty::getName));
		this.properties = ImmutableList.copyOf(properties);
		Map<Map<IProperty, Comparable>, StateImplementation> map = Maps.newLinkedHashMap();
		List<StateImplementation> list = new ArrayList<>();

		for (List<Comparable> list1 : Cartesian.cartesianProduct(getAllowedValues())) {
			Map<IProperty, Comparable> map1 = MapPopulator.createMap(this.properties, list1);
			StateImplementation blockstate$stateimplementation = new StateImplementation(b, ImmutableMap.copyOf(map1));
			map.put(map1, blockstate$stateimplementation);
			list.add(blockstate$stateimplementation);
		}

		for (StateImplementation state : list) state.buildPropertyValueTable(map);

		validStates = ImmutableList.copyOf(list);
	}

	public ImmutableList<IBlockState> getValidStates() {
		return validStates;
	}

	private List<Iterable<Comparable>> getAllowedValues() {
		List<Iterable<Comparable>> list = new ArrayList<>();

		for (int i = 0; i < properties.size(); ++i) {
			list.add(properties.get(i).getAllowedValues());
		}

		return list;
	}

	public IBlockState getBaseState() {
		return validStates.get(0);
	}

	public Block getBlock() {
		return block;
	}

	public Collection<IProperty> getProperties() {
		return properties;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("block", Block.blockRegistry.getNameForObject(block))
				.add("properties", properties.stream().map(GET_NAME_FUNC).collect(Collectors.toList())).toString();
	}

	static class StateImplementation extends BlockStateBase {

		private final Block block;
		private final ImmutableMap<IProperty, Comparable> properties;
		private ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;

		private StateImplementation(Block blockIn, ImmutableMap<IProperty, Comparable> propertiesIn) {
			block = blockIn;
			properties = propertiesIn;
		}

		public Collection<IProperty> getPropertyNames() {
			return Collections.unmodifiableCollection(properties.keySet());
		}

		public <T extends Comparable<T>> T getValue(IProperty<T> property) {
			if (!properties.containsKey(property)) {
				throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + block.getBlockState());
			}
			return property.getValueClass().cast(properties.get(property));
		}

		public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
			if (!properties.containsKey(property)) {
				throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + block.getBlockState());
			}
			if (!property.getAllowedValues().contains(value)) {
				throw new IllegalArgumentException(
						"Cannot set property " + property + " to " + value + " on block " +
								Block.blockRegistry.getNameForObject(block) + ", it is not an allowed value");
			}
			return properties.get(property) == value ? this : propertyValueTable.get(property, value);
		}

		public ImmutableMap<IProperty, Comparable> getProperties() {
			return properties;
		}

		public Block getBlock() {
			return block;
		}

		public boolean equals(Object p_equals_1_) {
			return this == p_equals_1_;
		}

		public int hashCode() {
			return properties.hashCode();
		}

		public void buildPropertyValueTable(Map<Map<IProperty, Comparable>, StateImplementation> map) {
			if (propertyValueTable != null) {
				throw new IllegalStateException();
			}
			Table<IProperty, Comparable, IBlockState> table = HashBasedTable.create();

			for (IProperty<? extends Comparable> iproperty : properties.keySet()) {
				for (Comparable comparable : iproperty.getAllowedValues()) {
					if (comparable != properties.get(iproperty)) {
						table.put(iproperty, comparable, map.get(getPropertiesWithValue(iproperty, comparable)));
					}
				}
			}

			propertyValueTable = ImmutableTable.copyOf(table);
		}

		private Map<IProperty, Comparable> getPropertiesWithValue(IProperty property, Comparable value) {
			Map<IProperty, Comparable> map = Maps.newHashMap(properties);
			map.put(property, value);
			return map;
		}

	}

}
