package vanilla.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.game.model.ModelIronGolem;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import vanilla.client.renderer.entity.vanilla.RenderIronGolem;
import net.minecraft.client.renderer.texture.TextureMap;
import vanilla.entity.monster.EntityIronGolem;
import net.minecraft.init.Blocks;

public class LayerIronGolemFlower implements LayerRenderer<EntityIronGolem>
{
    private final RenderIronGolem ironGolemRenderer;

    public LayerIronGolemFlower(RenderIronGolem ironGolemRendererIn)
    {
        this.ironGolemRenderer = ironGolemRendererIn;
    }

    public void doRenderLayer(EntityIronGolem entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (entitylivingbaseIn.getHoldRoseTick() != 0)
        {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            G.enableRescaleNormal();
            G.pushMatrix();
            G.rotate(5.0F + 180.0F * ((ModelIronGolem)this.ironGolemRenderer.getMainModel()).ironGolemRightArm.rotateAngleX / (float)Math.PI, 1.0F, 0.0F, 0.0F);
            G.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            G.translate(-0.9375F, -0.625F, -0.9375F);
            float f = 0.5F;
            G.scale(f, -f, f);
            int i = entitylivingbaseIn.getBrightnessForRender(partialTicks);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            G.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.ironGolemRenderer.bindTexture(TextureMap.locationBlocksTexture);
            blockrendererdispatcher.renderBlockBrightness(Blocks.red_flower.getDefaultState(), 1.0F);
            G.popMatrix();
            G.disableRescaleNormal();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
