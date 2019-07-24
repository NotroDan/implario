package net.minecraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.gui.HoverButton;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.Settings;

import java.util.List;

public class VolumeSlider extends SettingSlider implements HoverButton {

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
			this.sliderPosition = 1 - (float) (mouseY - this.yPosition) / this.height;

			if (this.sliderPosition < 0.0F) this.sliderPosition = 0.0F;
			if (this.sliderPosition > 1.0F) this.sliderPosition = 1.0F;

			settings.set(sliderPosition);
			settings.change();

		}

		int slider = (int) (yPosition + height * (1 - sliderPosition));
		int y = yPosition + height;

		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawHorizontalLine(xPosition, xPosition + 9, slider, -1);
		drawHorizontalLine(xPosition, xPosition + 9, slider + 1, -1);
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (!superPressed(mc, mouseX, mouseY)) return false;
		this.sliderPosition = 1 - (float) (mouseY - this.yPosition) / this.height;

		if (this.sliderPosition < 0.0F) this.sliderPosition = 0.0F;
		if (this.sliderPosition > 1.0F) this.sliderPosition = 1.0F;
		settings.set(sliderPosition);
		settings.change();

		this.isMouseDown = true;
		return true;
	}

	public boolean superPressed(Minecraft mc, int mouseX, int mouseY) {
		return this.enabled && this.visible &&
				mouseX >= this.xPosition - 5 &&
				mouseY >= this.yPosition - 12 &&
				mouseX < this.xPosition + this.width + 5 &&
				mouseY < this.yPosition + this.height + 26;
	}


	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (!this.visible) return;
		AssetsFontRenderer fontrenderer = mc.fontRenderer;
		mc.getTextureManager().bindTexture(buttonTextures);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = superPressed(mc, mouseX, mouseY);
		int i = this.getHoverState(this.hovered);
		G.enableBlend();
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		G.blendFunc(770, 771);

		int slider = (int) (yPosition + height * (1 - sliderPosition));
		int y = yPosition + height;

		if (hovered) {
			drawRect(xPosition - 5, yPosition - 12, xPosition + width + 5, yPosition + height + 26, 0x60f4c242);
		}

		drawRect(xPosition + 1, yPosition, xPosition + 9, y, 0xc0c7ffbf);
		drawRect(xPosition + 1, slider, xPosition + 9, y, 0xff48a334);
		if (settings != Settings.SOUND_MASTER) {
			int total = yPosition + (int) (height * (1 - sliderPosition * Settings.SOUND_MASTER.f()));
			drawRect(xPosition, total, xPosition + 10, total + 1, 0xfff3ff77);
		}
		drawCenteredString(Minecraft.getMinecraft().fontRenderer, (int) (sliderPosition * 100) + "%", xPosition + 5, yPosition - 10, -1);
		RenderHelper.enableGUIStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(settings.getSoundCategory().getItem(), xPosition - 3, yPosition + height + 8);
		RenderHelper.disableStandardItemLighting();


		this.mouseDragged(mc, mouseX, mouseY);
	}


	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.isMouseDown = false;
	}

	@Override
	public List<String> getHoverText() {
		return settings.getSoundCategory().getDescription();
	}

}
