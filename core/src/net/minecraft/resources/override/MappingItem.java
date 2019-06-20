package net.minecraft.resources.override;

import net.minecraft.item.Item;

public class MappingItem extends Mapping<Item> {

	public MappingItem(int id, String address, Item old, Item neo) {
		super(id, address, old, neo);
	}

	@Override
	public void map(int id, String address, Item element) {
		if (element == null) Item.itemRegistry.remove(Item.itemRegistry.getNameForObject(element));
		else Item.registerItem(id, address, element);
	}

}
