package vanilla.client.renderer.entity.layers;

import net.minecraft.client.game.model.ModelBase;
import net.minecraft.client.game.model.ModelSlime;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import vanilla.client.renderer.entity.vanilla.RenderSlime;
import vanilla.entity.monster.EntitySlime;

public class LayerSlimeGel implements LayerRenderer<EntitySlime>
{
    private final RenderSlime slimeRenderer;
    private final ModelBase slimeModel = new ModelSlime(0);

    public LayerSlimeGel(RenderSlime slimeRendererIn)
    {
        this.slimeRenderer = slimeRendererIn;
    }

    public void doRenderLayer(EntitySlime entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (!entitylivingbaseIn.isInvisible())
        {
            G.color(1.0F, 1.0F, 1.0F, 1.0F);
            G.enableNormalize();
            G.enableBlend();
            G.blendFunc(770, 771);
            this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
            this.slimeModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
            G.disableBlend();
            G.disableNormalize();
        }
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }
}
