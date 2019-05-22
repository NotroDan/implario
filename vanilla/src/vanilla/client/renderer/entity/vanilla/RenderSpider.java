package vanilla.client.renderer.entity.vanilla;

import net.minecraft.client.game.model.ModelSpider;
import vanilla.client.renderer.entity.RenderVanilla;
import net.minecraft.client.renderer.entity.RenderManager;
import vanilla.client.renderer.entity.layers.LayerSpiderEyes;
import vanilla.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;

public class RenderSpider<T extends EntitySpider> extends RenderVanilla<T> {

	private static final ResourceLocation spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");

	public RenderSpider(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelSpider(), 1.0F);
		this.addLayer(new LayerSpiderEyes(this));
	}

	protected float getDeathMaxRotation(T entityLivingBaseIn) {
		return 180.0F;
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(T entity) {
		return spiderTextures;
	}

}
