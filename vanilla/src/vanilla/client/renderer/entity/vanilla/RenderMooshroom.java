package vanilla.client.renderer.entity.vanilla;

import net.minecraft.client.game.model.ModelBase;
import vanilla.client.renderer.entity.RenderVanilla;
import net.minecraft.client.renderer.entity.RenderManager;
import vanilla.client.renderer.entity.layers.LayerMooshroomMushroom;
import vanilla.entity.passive.EntityMooshroom;
import net.minecraft.util.ResourceLocation;

public class RenderMooshroom extends RenderVanilla<EntityMooshroom> {

	private static final ResourceLocation mooshroomTextures = new ResourceLocation("textures/entity/cow/mooshroom.png");

	public RenderMooshroom(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
		this.addLayer(new LayerMooshroomMushroom(this));
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityMooshroom entity) {
		return mooshroomTextures;
	}

}
