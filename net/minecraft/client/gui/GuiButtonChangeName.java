package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonChangeName extends HoverButton {
	public GuiButtonChangeName(int buttonID, int xPos, int yPos) {
		super(buttonID, xPos, yPos, 20, 20, "", "Изменить имя");
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			mc.getTextureManager().bindTexture(buttonTextures);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int y = 206;
			if (flag) y += 20;

			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, y, this.width, this.height);
		}
	}
}
