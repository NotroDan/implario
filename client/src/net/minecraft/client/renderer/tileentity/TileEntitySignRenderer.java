package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.game.model.ModelSign;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.font.MCFontRenderer;
import net.minecraft.client.renderer.G;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.CustomColors;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TileEntitySignRenderer extends TileEntitySpecialRenderer<TileEntitySign> {

	private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");

	/**
	 * The ModelSign instance for use in this renderer
	 */
	private final ModelSign model = new ModelSign();


	public void renderTileEntityAt(TileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage) {
		Block block = te.getBlockType();
		G.pushMatrix();
		float f = 0.6666667F;

		if (block == Blocks.standing_sign) {
			G.translate((float) x + 0.5F, (float) y + 0.75F * f, (float) z + 0.5F);
			float f2 = (float) (te.getBlockMetadata() * 360) / 16.0F;
			G.rotate(-f2, 0.0F, 1.0F, 0.0F);
			this.model.signStick.showModel = true;
		} else {
			int k = te.getBlockMetadata();
			float f1 = 0.0F;

			if (k == 2) {
				f1 = 180.0F;
			}

			if (k == 4) {
				f1 = 90.0F;
			}

			if (k == 5) {
				f1 = -90.0F;
			}

			G.translate((float) x + 0.5F, (float) y + 0.75F * f, (float) z + 0.5F);
			G.rotate(-f1, 0.0F, 1.0F, 0.0F);
			G.translate(0.0F, -0.3125F, -0.4375F);
			this.model.signStick.showModel = false;
		}

		if (destroyStage >= 0) {
			this.bindTexture(DESTROY_STAGES[destroyStage]);
			G.matrixMode(5890);
			G.pushMatrix();
			G.scale(4.0F, 2.0F, 1.0F);
			G.translate(0.0625F, 0.0625F, 0.0625F);
			G.matrixMode(5888);
		} else {
			this.bindTexture(SIGN_TEXTURE);
		}

		G.enableRescaleNormal();
		G.pushMatrix();
		G.scale(f, -f, -f);
		this.model.renderSign();
		G.popMatrix();
		MCFontRenderer fontrenderer = this.getFontRenderer();
		float f3 = 0.015625F * f;
		G.translate(0.0F, 0.5F * f, 0.07F * f);
		G.scale(f3, -f3, f3);
		GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
		G.depthMask(false);
		int i = 0;

		if (Config.isCustomColors()) {
			i = CustomColors.getSignTextColor(i);
		}

		if (destroyStage < 0) {
			for (int j = 0; j < te.signText.length; ++j) {
				if (te.signText[j] != null) {
					IChatComponent ichatcomponent = te.signText[j];
					List list = GuiUtilRenderComponents.func_178908_a(ichatcomponent, 90, fontrenderer, false, true);
					String s = list != null && list.size() > 0 ? ((IChatComponent) list.get(0)).getFormattedText() : "";

					if (j == te.lineBeingEdited) {
						s = "> " + s + " <";
						fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, i);
					} else {
						fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, i);
					}
				}
			}
		}

		G.depthMask(true);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		G.popMatrix();

		if (destroyStage >= 0) {
			G.matrixMode(5890);
			G.popMatrix();
			G.matrixMode(5888);
		}
	}

}
