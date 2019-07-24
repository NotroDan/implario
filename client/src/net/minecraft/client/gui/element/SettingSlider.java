package net.minecraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.settings.SliderSetting;
import net.minecraft.util.MathHelper;

public class SettingSlider extends SettingButton {

	private float sliderValue;
	public boolean dragging;
	private SliderSetting options;
	private final float min;
	private final float max;

	public SettingSlider(int x, int y, Settings settings) {
		super(settings, x, y, 150, 20);
		if (!(settings.getBase() instanceof SliderSetting)) throw new IllegalArgumentException("Base setting is not a slider!");
		sliderValue = 1.0F;
		options = (SliderSetting) settings.getBase();
		this.min = options.getMin();
		this.max = options.getMax();
		Minecraft minecraft = Minecraft.getMinecraft();
		sliderValue = options.normalizeValue(options.value);
		displayString = getCaption(settings);
	}

	protected int getHoverState(boolean mouseOver) {
		return 0;
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
	 */
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		if (!visible) return;
		if (dragging) {
			sliderValue = (float) (mouseX - (xPosition + 4)) / (float) (width - 8);
			sliderValue = MathHelper.clamp_float(sliderValue, 0.0F, 1.0F);
			float f = options.denormalizeValue(sliderValue);
			options.value = f;
			sliderValue = options.normalizeValue(f);
			displayString = getCaption(settings);
		}

		mc.getTextureManager().bindTexture(buttonTextures);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		int sliderX = xPosition + (int) (sliderValue * (width - 8));
		if (Settings.FANCY_BUTTONS.b()) {
			boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int border = 0xc0636363;
			if (hovered) border = 0xd0e06c14;
			drawRect(sliderX, yPosition, sliderX + 8, yPosition + 20, border);
			drawRect(sliderX + 1, yPosition + 1, sliderX + 7, yPosition + 19, 0xd0898989);
		} else {
			drawTexturedModalRect(xPosition + (int) (sliderValue * (float) (width - 8)), yPosition, 0, 66, 4, 20);
			drawTexturedModalRect(xPosition + (int) (sliderValue * (float) (width - 8)) + 4, yPosition, 196, 66, 4, 20);
		}
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (!super.mousePressed(mc, mouseX, mouseY)) return false;
		sliderValue = (float) (mouseX - (xPosition + 4)) / (float) (width - 8);
		sliderValue = MathHelper.clamp_float(sliderValue, 0.0F, 1.0F);
		options.value = options.denormalizeValue(sliderValue);
		displayString = getCaption(settings);
		dragging = true;
		return true;
	}


	/**
	 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
	 */
	public void mouseReleased(int mouseX, int mouseY) {
		dragging = false;
//		if (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) click();
	}

}
