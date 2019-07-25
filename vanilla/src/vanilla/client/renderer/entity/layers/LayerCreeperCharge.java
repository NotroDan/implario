package vanilla.client.renderer.entity.layers;

import vanilla.client.game.model.ModelCreeper;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import vanilla.client.renderer.entity.vanilla.RenderCreeper;
import vanilla.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

public class LayerCreeperCharge implements LayerRenderer<EntityCreeper> {

	private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
	private final RenderCreeper creeperRenderer;
	private final ModelCreeper creeperModel = new ModelCreeper(2.0F);

	public LayerCreeperCharge(RenderCreeper creeperRendererIn) {
		this.creeperRenderer = creeperRendererIn;
	}

	public void doRenderLayer(EntityCreeper entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (entitylivingbaseIn.getPowered()) {
			boolean flag = entitylivingbaseIn.isInvisible();
			G.depthMask(!flag);
			this.creeperRenderer.bindTexture(LIGHTNING_TEXTURE);
			G.matrixMode(5890);
			G.loadIdentity();
			float f = (float) entitylivingbaseIn.ticksExisted + partialTicks;
			G.translate(f * 0.01F, f * 0.01F, 0.0F);
			G.matrixMode(5888);
			G.enableBlend();
			float f1 = 0.5F;
			G.color(f1, f1, f1, 1.0F);
			G.disableLighting();
			G.blendFunc(1, 1);
			this.creeperModel.setModelAttributes(this.creeperRenderer.getMainModel());
			this.creeperModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
			G.matrixMode(5890);
			G.loadIdentity();
			G.matrixMode(5888);
			G.enableLighting();
			G.disableBlend();
			G.depthMask(flag);
		}
	}

	public boolean shouldCombineTextures() {
		return false;
	}

}
