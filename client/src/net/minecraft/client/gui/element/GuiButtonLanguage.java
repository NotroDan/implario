package net.minecraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.HoverButton;
import net.minecraft.client.renderer.G;

import java.util.Collections;
import java.util.List;

public class GuiButtonLanguage extends GuiButton implements HoverButton {

	private static final List<String> text = Collections.singletonList("Изменить язык");

	public GuiButtonLanguage(int buttonID, int xPos, int yPos) {
		super(buttonID, xPos, yPos, 20, 20, "");
	}

	@Override
	public List<String> getHoverText() {
		return text;
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
			G.color(1.0F, 1.0F, 1.0F, 1.0F);
			boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int i = 106;

			if (flag) {
				i += this.height;
			}

			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, i, this.width, this.height);
		}
	}

}
