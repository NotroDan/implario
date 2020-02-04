package vanilla.client.renderer.entity.layers;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import vanilla.client.renderer.entity.vanilla.RenderEnderman;
import net.minecraft.client.renderer.texture.TextureMap;
import vanilla.entity.monster.EntityEnderman;

public class LayerHeldBlock implements LayerRenderer<EntityEnderman> {

	private final RenderEnderman endermanRenderer;

	public LayerHeldBlock(RenderEnderman endermanRendererIn) {
		this.endermanRenderer = endermanRendererIn;
	}

	public void doRenderLayer(EntityEnderman entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		IBlockState iblockstate = entitylivingbaseIn.getHeldBlockState();

		if (iblockstate.getBlock().getMaterial() != Material.air) {
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.get().getBlockRendererDispatcher();
			G.enableRescaleNormal();
			G.pushMatrix();
			G.translate(0.0F, 0.6875F, -0.75F);
			G.rotate(20.0F, 1.0F, 0.0F, 0.0F);
			G.rotate(45.0F, 0.0F, 1.0F, 0.0F);
			G.translate(0.25F, 0.1875F, 0.25F);
			float f = 0.5F;
			G.scale(-f, -f, f);
			int i = entitylivingbaseIn.getBrightnessForRender(partialTicks);
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
			G.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.endermanRenderer.bindTexture(TextureMap.locationBlocksTexture);
			blockrendererdispatcher.renderBlockBrightness(iblockstate, 1.0F);
			G.popMatrix();
			G.disableRescaleNormal();
		}
	}

	public boolean shouldCombineTextures() {
		return false;
	}

}
