package net.minecraft.client.gui.element;

import net.minecraft.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.Settings;

public class VolumeSlider extends SettingSlider {

	private float sliderPosition;
	public boolean isMouseDown;

	public VolumeSlider(Settings settings, int x, int y) {
		super(x, y, settings);
		sliderPosition = settings.f();
		height = 20;
	}

	public float getSliderPosition() {
		return this.sliderPosition;
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		return 0;
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
	 */
	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			if (this.isMouseDown) {
				this.sliderPosition = (float) (mouseY - this.yPosition / this.height);

				if (this.sliderPosition < 0.0F) {
					this.sliderPosition = 0.0F;
				}

				if (this.sliderPosition > 1.0F) {
					this.sliderPosition = 1.0F;
				}

			}

			int slider = (int) (yPosition - 20 * sliderPosition);

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			drawRect(xPosition - 2, yPosition - 22, xPosition + 2, yPosition + 2, 0xb071ff51);
			drawRect(xPosition - 2, slider, xPosition + 2, yPosition + 2, 0xc048a334);
			drawRect(xPosition - 3, slider - 2, xPosition + 3, slider + 2, 0xd098ff82);
//			this.drawTexturedModalRect(this.xPosition + (int) (this.sliderPosition * (float) (this.width - 8)), this.yPosition, 0, 66, 4, 20);
//			this.drawTexturedModalRect(this.xPosition + (int) (this.sliderPosition * (float) (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
		}
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (!super.mousePressed(mc, mouseX, mouseY)) return false;
		this.sliderPosition = (float) (mouseY - this.yPosition / this.height);

		if (this.sliderPosition < 0.0F) {
			this.sliderPosition = 0.0F;
		}

		if (this.sliderPosition > 1.0F) {
			this.sliderPosition = 1.0F;
		}

		this.isMouseDown = true;
		return true;
	}


	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!this.visible) return;
		FontRenderer fontrenderer = mc.fontRendererObj;
		mc.getTextureManager().bindTexture(buttonTextures);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		int i = this.getHoverState(this.hovered);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.blendFunc(770, 771);


		if (Settings.FANCY_BUTTONS.b()) {

			int color = i == 0 ? 0xd03f3f3f : 0xd04f4f4f;
			int borderColor = 0xd0707070;
			if (i == 2) {
				if (Settings.RAINBOW_SHIT.b()) borderColor = Utils.rainbowGradient((int) (System.currentTimeMillis() % 1536));
				else borderColor = 0xd0e06c14;
			}
			int border = 1;

			drawRect(xPosition, yPosition, xPosition + width, yPosition + height, borderColor);
			drawRect(xPosition + border, yPosition + border, xPosition + width - border, yPosition + height - border, color);

		} else {
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		}
		this.mouseDragged(mc, mouseX, mouseY);
		int j = 0xe0e0e0;
		if (!enabled) j = 0xa0a0a0;
		else if (hovered) j = 0xffffa0;

		this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
	}


	/**
	 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.isMouseDown = false;
	}

}
