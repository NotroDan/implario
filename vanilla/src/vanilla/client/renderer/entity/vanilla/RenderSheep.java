package vanilla.client.renderer.entity.vanilla;

import net.minecraft.client.game.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import vanilla.client.renderer.entity.RenderVanilla;
import vanilla.client.renderer.entity.layers.LayerSheepWool;
import vanilla.entity.passive.EntitySheep;

public class RenderSheep extends RenderVanilla<EntitySheep> {

	private static final ResourceLocation shearedSheepTextures = new ResourceLocation("textures/entity/sheep/sheep.png");

	public RenderSheep(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
		this.addLayer(new LayerSheepWool(this));
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntitySheep entity) {
		return shearedSheepTextures;
	}

}
