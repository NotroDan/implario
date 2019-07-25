package vanilla.client.renderer.entity.vanilla;

import vanilla.client.game.model.ModelBlaze;
import vanilla.client.renderer.entity.RenderVanilla;
import net.minecraft.client.renderer.entity.RenderManager;
import vanilla.entity.monster.EntityBlaze;
import net.minecraft.util.ResourceLocation;

public class RenderBlaze extends RenderVanilla<EntityBlaze> {

	private static final ResourceLocation blazeTextures = new ResourceLocation("textures/entity/blaze.png");

	public RenderBlaze(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelBlaze(), 0.5F);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityBlaze entity) {
		return blazeTextures;
	}

}
