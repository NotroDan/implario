package net.minecraft.client.resources;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.resources.mapping.AbstractMapping;
import net.minecraft.resources.mapping.Mapping;
import net.minecraft.tileentity.TileEntity;

public class MappingTileEntityRenderer<T extends TileEntity> extends AbstractMapping<TileEntitySpecialRenderer<T>> {

	private final Class<T> type;

	public MappingTileEntityRenderer(Class<T> type, TileEntitySpecialRenderer<T> renderer) {
		super(type.getSimpleName(), TileEntityRendererDispatcher.instance.getSpecialRendererByClass(type), renderer);
		this.type = type;
	}

	@Override
	protected void map(TileEntitySpecialRenderer<T> element) {
		if (element == null) TileEntityRendererDispatcher.instance.getMapSpecialRenderers().remove(type);
		else TileEntityRendererDispatcher.instance.getMapSpecialRenderers().put(type, element);
	}

}
