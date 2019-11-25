package net.minecraft.block.state;

import com.google.common.base.Objects;
import com.google.common.collect.*;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

import java.util.*;
import java.util.stream.Collectors;

public class BlockState {
	private final Block block;
	private final ImmutableList<IProperty> properties;
	private final ImmutableList<IBlockState> validStates;

	public BlockState(Block b, IProperty... properties) {
		block = b;
		Arrays.sort(properties, Comparator.comparing(IProperty::getName));
		this.properties = ImmutableList.copyOf(properties);
		Map<Map<IProperty, Comparable>, StateImplementation> map = Maps.newLinkedHashMap();
		List<StateImplementation> list = new ArrayList<>();

		List<Iterable<Comparable>> lst = getAllowedValues();
		if(lst.size() == 0){
			ImmutableMap<IProperty, Comparable> map1 = ImmutableMap.of();
			StateImplementation blockstate$stateimplementation = new StateImplementation(block, map1);
			map.put(map1, blockstate$stateimplementation);
			list.add(blockstate$stateimplementation);
		}else register(lst, 0, list, map, new Comparable[]{});

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

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("block", Block.blockRegistry.getNameForObject(block))
				.add("properties", properties.stream().map(s -> s == null ? "<NULL>" : s.getName())
						.collect(Collectors.toList())).toString();
	}

	private static class StateImplementation extends BlockStateBase {
		private final Block block;
		private final ImmutableMap<IProperty, Comparable> properties;
		private ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;
		private int id;

		private StateImplementation(Block blockIn, ImmutableMap<IProperty, Comparable> propertiesIn) {
			block = blockIn;
			properties = propertiesIn;
		}

		@Override
		public int getID() {
			return id;
		}

		@Override
		public void setID(int id) {
			this.id = id;
		}

		@Override
		public Collection<IProperty> getPropertyNames() {
			return Collections.unmodifiableCollection(properties.keySet());
		}

		@Override
		public <T extends Comparable<T>> T getValue(IProperty<T> property) {
			Comparable comparable = properties.get(property);
			if (comparable == null)
				throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + block.getBlockState());
			return property.getValueClass().cast(comparable);
		}

		@Override
		public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
			Comparable comparable = properties.get(property);
			if (comparable == null)
				throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + block.getBlockState());
			if (!property.getAllowedValues().contains(value))
				throw new IllegalArgumentException(
						"Cannot set property " + property + " to " + value + " on block " +
								Block.blockRegistry.getNameForObject(block) + ", it is not an allowed value");

			return comparable == value ? this : propertyValueTable.get(property, value);
		}

		@Override
		public ImmutableMap<IProperty, Comparable> getProperties() {
			return properties;
		}

		@Override
		public Block getBlock() {
			return block;
		}

		@Override
		public int hashCode() {
			return properties.hashCode();
		}

		private void buildPropertyValueTable(Map<Map<IProperty, Comparable>, StateImplementation> map) {
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

	private void register(List<Iterable<Comparable>> iterables, int index, List<StateImplementation> list,
						  Map<Map<IProperty, Comparable>, StateImplementation> map, Comparable[] reg){
		Iterable<Comparable> iterable = iterables.get(index);
		if(iterables.size() == index + 1){
			for(Comparable comparable : iterable){
				Map<IProperty, Comparable> map1 = new LinkedHashMap<>();
				int i = reg.length - 1;
				for(IProperty property : properties) {
					Comparable comp = i == -1 ? comparable : reg[i];
					map1.put(property, comp);
					i--;
				}


				StateImplementation blockstate$stateimplementation = new StateImplementation(block, ImmutableMap.copyOf(map1));
				map.put(map1, blockstate$stateimplementation);
				list.add(blockstate$stateimplementation);
			}
		}else{
			index++;
			for(Comparable comparable : iterable){
				Comparable newComparable[] = new Comparable[reg.length + 1];
				System.arraycopy(reg, 0, newComparable, 1, reg.length);
				newComparable[0] = comparable;
				register(iterables, index, list, map, newComparable);
			}
		}
	}
}
