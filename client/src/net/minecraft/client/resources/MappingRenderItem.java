package net.minecraft.client.resources;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.resources.mapping.Mapping;

public class MappingRenderItem extends Mapping<ModelResourceLocation> {

	private final ItemModelMesher modelMesher;
	private final Item item;
	private final int meta;

	public MappingRenderItem(ItemModelMesher mesher, Item item, int meta, ModelResourceLocation updated, ModelResourceLocation existing) {
		super(updated.getResourcePath(), existing, updated);
		this.item = item;
		this.meta = meta;
		this.modelMesher = mesher;
	}

	@Override
	protected void map(ModelResourceLocation element) {
		if (element == null) modelMesher.unregisterModelLocation(item, meta);
		else modelMesher.registerModelLocation(item, meta, element);
	}

}
