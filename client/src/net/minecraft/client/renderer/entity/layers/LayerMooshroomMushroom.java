package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.game.model.ModelQuadruped;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.vanilla.RenderMooshroom;
import net.minecraft.client.renderer.texture.TextureMap;
import vanilla.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;

public class LayerMooshroomMushroom implements LayerRenderer<EntityMooshroom>
{
    private final RenderMooshroom mooshroomRenderer;

    public LayerMooshroomMushroom(RenderMooshroom mooshroomRendererIn)
    {
        this.mooshroomRenderer = mooshroomRendererIn;
    }

    public void doRenderLayer(EntityMooshroom entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (!entitylivingbaseIn.isChild() && !entitylivingbaseIn.isInvisible())
        {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            this.mooshroomRenderer.bindTexture(TextureMap.locationBlocksTexture);
            G.enableCull();
            G.cullFace(1028);
            G.pushMatrix();
            G.scale(1.0F, -1.0F, 1.0F);
            G.translate(0.2F, 0.35F, 0.5F);
            G.rotate(42.0F, 0.0F, 1.0F, 0.0F);
            G.pushMatrix();
            G.translate(-0.5F, -0.5F, 0.5F);
            blockrendererdispatcher.renderBlockBrightness(Blocks.red_mushroom.getDefaultState(), 1.0F);
            G.popMatrix();
            G.pushMatrix();
            G.translate(0.1F, 0.0F, -0.6F);
            G.rotate(42.0F, 0.0F, 1.0F, 0.0F);
            G.translate(-0.5F, -0.5F, 0.5F);
            blockrendererdispatcher.renderBlockBrightness(Blocks.red_mushroom.getDefaultState(), 1.0F);
            G.popMatrix();
            G.popMatrix();
            G.pushMatrix();
            ((ModelQuadruped)this.mooshroomRenderer.getMainModel()).head.postRender(0.0625F);
            G.scale(1.0F, -1.0F, 1.0F);
            G.translate(0.0F, 0.7F, -0.2F);
            G.rotate(12.0F, 0.0F, 1.0F, 0.0F);
            G.translate(-0.5F, -0.5F, 0.5F);
            blockrendererdispatcher.renderBlockBrightness(Blocks.red_mushroom.getDefaultState(), 1.0F);
            G.popMatrix();
            G.cullFace(1029);
            G.disableCull();
        }
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }
}
