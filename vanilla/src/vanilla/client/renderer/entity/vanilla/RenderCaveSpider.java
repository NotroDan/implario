package vanilla.client.renderer.entity.vanilla;

import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import vanilla.entity.monster.EntityCaveSpider;

public class RenderCaveSpider extends RenderSpider<EntityCaveSpider> {

	private static final ResourceLocation caveSpiderTextures = new ResourceLocation("textures/entity/spider/cave_spider.png");

	public RenderCaveSpider(RenderManager renderManagerIn) {
		super(renderManagerIn);
		this.shadowSize *= 0.7F;
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
	 * entityLiving, partialTickTime
	 */
	protected void preRenderCallback(EntityCaveSpider entitylivingbaseIn, float partialTickTime) {
		G.scale(0.7F, 0.7F, 0.7F);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityCaveSpider entity) {
		return caveSpiderTextures;
	}

}
