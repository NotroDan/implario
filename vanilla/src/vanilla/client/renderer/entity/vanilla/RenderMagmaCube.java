package vanilla.client.renderer.entity.vanilla;

import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import vanilla.client.game.model.ModelMagmaCube;
import vanilla.client.renderer.entity.RenderVanilla;
import vanilla.entity.monster.EntityMagmaCube;

public class RenderMagmaCube extends RenderVanilla<EntityMagmaCube> {

	private static final ResourceLocation magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");

	public RenderMagmaCube(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelMagmaCube(), 0.25F);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityMagmaCube entity) {
		return magmaCubeTextures;
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
	 * entityLiving, partialTickTime
	 */
	protected void preRenderCallback(EntityMagmaCube entitylivingbaseIn, float partialTickTime) {
		int i = entitylivingbaseIn.getSlimeSize();
		float f = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / ((float) i * 0.5F + 1.0F);
		float f1 = 1.0F / (f + 1.0F);
		float f2 = (float) i;
		G.scale(f1 * f2, 1.0F / f1 * f2, f1 * f2);
	}

}
