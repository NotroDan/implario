package vanilla.client.renderer.entity.layers;

import vanilla.client.game.model.ModelWither;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import vanilla.client.renderer.entity.vanilla.RenderWither;
import vanilla.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerWitherAura implements LayerRenderer<EntityWither>
{
    private static final ResourceLocation WITHER_ARMOR = new ResourceLocation("textures/entity/wither/wither_armor.png");
    private final RenderWither witherRenderer;
    private final ModelWither witherModel = new ModelWither(0.5F);

    public LayerWitherAura(RenderWither witherRendererIn)
    {
        this.witherRenderer = witherRendererIn;
    }

    public void doRenderLayer(EntityWither entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (entitylivingbaseIn.isArmored())
        {
            G.depthMask(!entitylivingbaseIn.isInvisible());
            this.witherRenderer.bindTexture(WITHER_ARMOR);
            G.matrixMode(5890);
            G.loadIdentity();
            float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
            float f1 = MathHelper.cos(f * 0.02F) * 3.0F;
            float f2 = f * 0.01F;
            G.translate(f1, f2, 0.0F);
            G.matrixMode(5888);
            G.enableBlend();
            float f3 = 0.5F;
            G.color(f3, f3, f3, 1.0F);
            G.disableLighting();
            G.blendFunc(1, 1);
            this.witherModel.setLivingAnimations(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks);
            this.witherModel.setModelAttributes(this.witherRenderer.getMainModel());
            this.witherModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
            G.matrixMode(5890);
            G.loadIdentity();
            G.matrixMode(5888);
            G.enableLighting();
            G.disableBlend();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
