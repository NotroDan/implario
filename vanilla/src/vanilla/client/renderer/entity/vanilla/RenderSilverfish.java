package vanilla.client.renderer.entity.vanilla;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import vanilla.client.game.model.ModelSilverfish;
import vanilla.client.renderer.entity.RenderVanilla;
import vanilla.entity.monster.EntitySilverfish;

public class RenderSilverfish extends RenderVanilla<EntitySilverfish> {

	private static final ResourceLocation silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");

	public RenderSilverfish(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelSilverfish(), 0.3F);
	}

	protected float getDeathMaxRotation(EntitySilverfish entityLivingBaseIn) {
		return 180.0F;
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntitySilverfish entity) {
		return silverfishTextures;
	}

}
