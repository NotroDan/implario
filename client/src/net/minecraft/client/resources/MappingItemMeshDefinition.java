package net.minecraft.client.resources;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.item.Item;
import net.minecraft.resources.mapping.Mapping;

public class MappingItemMeshDefinition extends Mapping<ItemMeshDefinition> {

	private final ItemModelMesher modelMesher;
	private final Item item;

	public MappingItemMeshDefinition(ItemModelMesher itemModelMesher, Item item, ItemMeshDefinition definition) {
		super(item.getUnlocalizedName(), itemModelMesher.getShapers().get(item), definition);
		this.modelMesher = itemModelMesher;
		this.item = item;
	}

	@Override
	protected void map(ItemMeshDefinition element) {
		if (element == null) modelMesher.getShapers().remove(item);
		else modelMesher.getShapers().put(item, element);
	}

}
