package vanilla.client.renderer.entity.vanilla;

import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import vanilla.client.game.model.ModelBat;
import vanilla.client.renderer.entity.RenderVanilla;
import vanilla.entity.passive.EntityBat;

public class RenderBat extends RenderVanilla<EntityBat> {

	private static final ResourceLocation batTextures = new ResourceLocation("textures/entity/bat.png");

	public RenderBat(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelBat(), 0.25F);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityBat entity) {
		return batTextures;
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
	 * entityLiving, partialTickTime
	 */
	protected void preRenderCallback(EntityBat entitylivingbaseIn, float partialTickTime) {
		G.scale(0.35F, 0.35F, 0.35F);
	}

	protected void rotateCorpse(EntityBat bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
		if (!bat.getIsBatHanging()) {
			G.translate(0.0F, MathHelper.cos(p_77043_2_ * 0.3F) * 0.1F, 0.0F);
		} else {
			G.translate(0.0F, -0.1F, 0.0F);
		}

		super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
	}

}
