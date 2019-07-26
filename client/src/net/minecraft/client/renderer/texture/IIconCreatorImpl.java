package net.minecraft.client.renderer.texture;

import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

public class IIconCreatorImpl implements IIconCreator {

	private final Set<ResourceLocation> set;
	private final ModelBakery modelbakery;

	public IIconCreatorImpl(Set<ResourceLocation> set, ModelBakery modelbakery) {
		this.set = set;
		this.modelbakery = modelbakery;
	}

	@Override
	public void registerSprites(TextureMap iconRegistry) {
		for (ResourceLocation resourcelocation : set) {
			TextureAtlasSprite textureatlassprite = iconRegistry.registerSprite(resourcelocation);
			modelbakery.sprites.put(resourcelocation, textureatlassprite);
		}
	}

}
