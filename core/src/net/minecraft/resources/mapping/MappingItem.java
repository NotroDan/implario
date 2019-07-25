package net.minecraft.resources.mapping;

import net.minecraft.item.Item;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;

public class MappingItem extends LegacyMapping<Item> {

	public MappingItem(int id, String address, Item old, Item neo) {
		super(id, address, old, neo);
	}

	@Override
	public void map(Item element) {
		if (element == null) {
			RegistryNamespaced<ResourceLocation, Item> r = Item.itemRegistry;
			r.remove(r.getNameForObject(r.getObjectById(id)));
		} else Item.registerItem(id, address, element);
	}

}
