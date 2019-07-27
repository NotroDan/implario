package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.element.Colors;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.gui.element.RenderRec;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.Player;

import java.io.IOException;

public class GuiInventory extends InventoryEffectRenderer {

	private float oldMouseX;
	private float oldMouseY;

	public GuiInventory(Player player) {
		super(player.inventoryContainer);
		this.allowUserInput = true;
	}

	@Override
	public void updateScreen() {
		if (mc.playerController.isInCreativeMode())
			mc.displayGuiScreen(new GuiContainerCreative(mc.thePlayer));
		updateActivePotionEffects();
	}

	@Override
	public void initGui() {
		buttonList.clear();

		if (mc.playerController.isInCreativeMode())
			mc.displayGuiScreen(new GuiContainerCreative(mc.thePlayer));
		else
			super.initGui();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(Lang.format("container.crafting"), 88, 14, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		oldMouseX = (float) mouseX;
		oldMouseY = (float) mouseY;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int x = guiLeft;
		int y = guiTop;
		if (Settings.MODERN_INVENTORIES.b()) {
			x = x + 4;
			y = y + 4;
			RenderRec.render(x, y, 168, 158, Colors.DARK);

			if (Settings.SLOT_GRID.i() != 2) {
				RenderRec.render(x + 2, y + 2, 20, 74, Colors.DARK_GRAY);
				RenderRec.render(x + 82, y + 20, 38, 38, Colors.DARK_GRAY);
				RenderRec.render(x + 138, y + 30, 20, 20, Colors.DARK_GRAY);
				RenderRec.render(x + 2, y + 78, 164, 56, Colors.DARK_GRAY);
				RenderRec.render(x + 2, y + 136, 164, 20, Colors.DARK_GRAY);
				RenderRec.render(x + 26, y + 2, 50, 74, Colors.DARK_GRAY);
			}

			RenderRec.render(x + 28, y + 4, 46, 70, Colors.BLACK);
			RenderRec.render(x + 122, y + 38, 10, 4, Colors.GRAY);
			int x1 = x + 132, y1 = y + 34;
			Gui.drawTriangle(x1, y1, x1, y1 + 12, x1 + 6, y1 + 6, Colors.GRAY);
		} else {
			mc.getTextureManager().bindTexture(inventoryBackground);
			drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		}
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawEntityOnScreen(x += 50, y + 68, 30, (float) x - this.oldMouseX,
				(float) (y + 17) - this.oldMouseY, this.mc.thePlayer);
	}

	/**
	 * Draws the entity to the screen. Args: xPos, yPos, scale, mouseX, mouseY, entityLiving
	 */
	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
		G.enableColorMaterial();
		G.pushMatrix();
		G.translate((float) posX, (float) posY, 50.0F);
		G.scale((float) -scale, (float) scale, (float) scale);
		G.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		G.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		G.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		G.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
		ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
		ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		G.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		rendermanager.setRenderShadow(true);
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		G.popMatrix();
		RenderHelper.disableStandardItemLighting();
		G.disableRescaleNormal();
		G.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		G.disableTexture2D();
		G.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
		}
	}

}
