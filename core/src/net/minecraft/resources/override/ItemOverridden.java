package net.minecraft.resources.override;

import net.minecraft.item.Item;

public class ItemOverridden extends OverriddenEntry<Item> {

	public ItemOverridden(int id, String address, Item old, Item neo) {
		super(id, address, old, neo);
	}

	@Override
	public void override(int id, String address, Item element) {
		Item.registerItem(id, address, element);
	}

}
