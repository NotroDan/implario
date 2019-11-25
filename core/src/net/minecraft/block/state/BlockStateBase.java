package net.minecraft.block.state;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;

public abstract class BlockStateBase implements IBlockState {
	private int metadata = -1;
	private ResourceLocation blockLocation = null;

	public int getMetadata() {
		if (this.metadata < 0) {
			this.metadata = this.getBlock().getMetaFromState(this);
		}

		return this.metadata;
	}

	public ResourceLocation getBlockLocation() {
		if (this.blockLocation == null) {
			this.blockLocation = (ResourceLocation) Block.blockRegistry.getNameForObject(this.getBlock());
		}

		return this.blockLocation;
	}

	/**
	 * Create a version of this BlockState with the given property cycled to the next value in order. If the property
	 * was at the highest possible value, it is set to the lowest one instead.
	 */
	@Override
	public IBlockState cycleProperty(IProperty property) {
		return this.withProperty(property, (Comparable) cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
	}

	@Override
	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(Block.blockRegistry.getNameForObject(this.getBlock()));

		if (!getProperties().isEmpty()) {
			stringbuilder.append("[");
			for(Entry<IProperty, Comparable> entry : getProperties().entrySet()){
				if(entry == null){
					stringbuilder.append("<NULL>");
					continue;
				}
				IProperty property = entry.getKey();
				stringbuilder.append(property.getName()).append("=").append(property.getName(entry.getValue()));
			}
		}

		return stringbuilder.toString();
	}

	/**
	 * Helper method for cycleProperty.
	 */
	private static Object cyclePropertyValue(Collection values, Object currentValue) {
		Iterator iterator = values.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equals(currentValue)) {
				if (iterator.hasNext()) {
					return iterator.next();
				}

				return values.iterator().next();
			}
		}

		return iterator.next();
	}
}
