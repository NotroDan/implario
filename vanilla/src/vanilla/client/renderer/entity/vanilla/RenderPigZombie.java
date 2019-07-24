package vanilla.client.renderer.entity.vanilla;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import vanilla.client.game.model.ModelZombie;
import vanilla.client.renderer.entity.RenderBiped;
import vanilla.entity.monster.EntityPigZombie;

public class RenderPigZombie extends RenderBiped<EntityPigZombie> {

	private static final ResourceLocation ZOMBIE_PIGMAN_TEXTURE = new ResourceLocation("textures/entity/zombie_pigman.png");

	public RenderPigZombie(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelZombie(), 0.5F, 1.0F);
		this.addLayer(new LayerHeldItem(this));
		this.addLayer(new LayerBipedArmor(this) {
			protected void initArmor() {
				this.field_177189_c = new ModelZombie(0.5F, true);
				this.field_177186_d = new ModelZombie(1.0F, true);
			}
		});
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityPigZombie entity) {
		return ZOMBIE_PIGMAN_TEXTURE;
	}

}
