package net.minecraft.client.gui.element;

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
		height = 100;
		width = 10;
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
		if (!this.visible) return;
		if (this.isMouseDown) {
			this.sliderPosition = (float) (mouseY - this.yPosition) / this.height;

			if (this.sliderPosition < 0.0F) this.sliderPosition = 0.0F;
			if (this.sliderPosition > 1.0F) this.sliderPosition = 1.0F;

			settings.set(sliderPosition);

		}

		int slider = (int) (yPosition + height * sliderPosition);
		int y = yPosition + height;

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawHorizontalLine(xPosition, xPosition + 1, slider,-1);
		drawHorizontalLine(xPosition, xPosition + 1, slider + 1,-1);
		//			this.drawTexturedModalRect(this.xPosition + (int) (this.sliderPosition * (float) (this.width - 8)), this.yPosition, 0, 66, 4, 20);
		//			this.drawTexturedModalRect(this.xPosition + (int) (this.sliderPosition * (float) (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (!super.mousePressed(mc, mouseX, mouseY)) return false;
		this.sliderPosition = (float) (mouseY - this.yPosition) / this.height;

		if (this.sliderPosition < 0.0F) this.sliderPosition = 0.0F;
		if (this.sliderPosition > 1.0F) this.sliderPosition = 1.0F;
		settings.set(sliderPosition);

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

		int slider = (int) (yPosition + height * sliderPosition);
		int y = yPosition + height;
		drawRect(xPosition + 1, yPosition, xPosition + 9, y, 0xc0c7ffbf);
		drawRect(xPosition + 1, slider, xPosition + 9, y, 0xff48a334);

		this.mouseDragged(mc, mouseX, mouseY);
	}


	/**
	 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.isMouseDown = false;
	}

}
