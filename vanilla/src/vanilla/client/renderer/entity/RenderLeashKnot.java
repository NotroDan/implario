package vanilla.client.renderer.entity;

import vanilla.client.game.model.ModelLeashKnot;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import vanilla.entity.EntityLeashKnot;

public class RenderLeashKnot extends Render<EntityLeashKnot> {

	private static final ResourceLocation leashKnotTextures = new ResourceLocation("textures/entity/lead_knot.png");
	private ModelLeashKnot leashKnotModel = new ModelLeashKnot();

	public RenderLeashKnot(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doe
	 */
	public void doRender(EntityLeashKnot entity, double x, double y, double z, float entityYaw, float partialTicks) {
		G.pushMatrix();
		G.disableCull();
		G.translate((float) x, (float) y, (float) z);
		float f = 0.0625F;
		G.enableRescaleNormal();
		G.scale(-1.0F, -1.0F, 1.0F);
		G.enableAlpha();
		this.bindEntityTexture(entity);
		this.leashKnotModel.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, f);
		G.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityLeashKnot entity) {
		return leashKnotTextures;
	}

}
