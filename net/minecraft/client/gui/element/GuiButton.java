package net.minecraft.client.gui.element;

import net.minecraft.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.Settings;
import net.minecraft.util.ResourceLocation;

public class GuiButton extends Gui {

	public static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

	/**
	 * Button width in pixels
	 */
	public int width;

	/**
	 * Button height in pixels
	 */
	public int height;

	/**
	 * The x position of this control.
	 */
	public int xPosition;

	/**
	 * The y position of this control.
	 */
	public int yPosition;

	/**
	 * The string displayed on this control.
	 */
	public String displayString;
	public int id;

	/**
	 * True if this control is enabled, false to disable.
	 */
	public boolean enabled;

	/**
	 * Hides the button completely if false.
	 */
	public boolean visible;
	public boolean hovered;

	public GuiButton(int buttonId, int x, int y, String buttonText) {
		this(buttonId, x, y, 200, 20, buttonText);
	}

	public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		this.width = 200;
		this.height = 20;
		this.enabled = true;
		this.visible = true;
		this.id = buttonId;
		this.xPosition = x;
		this.yPosition = y;
		this.width = widthIn;
		this.height = heightIn;
		this.displayString = buttonText;
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
	 * this button.
	 */
	protected int getHoverState(boolean mouseOver) {
		return !this.enabled ? 0 : mouseOver ? 2 : 1;
	}

	/**
	 * Draws this button to the screen.
	 */
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
		int j = 14737632;
		if (!enabled) j = 10526880;
		else if (hovered) j = 16777120;

		this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
	 */
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
	}

	/**
	 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
	 */
	public void mouseReleased(int mouseX, int mouseY) {
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}

	/**
	 * Whether the mouse cursor is currently over the button.
	 */
	public boolean isMouseOver() {
		return this.hovered;
	}

	public void drawButtonForegroundLayer(int mouseX, int mouseY) {
	}

	public void playPressSound(SoundHandler soundHandlerIn) {
		soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
	}

	public int getButtonWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
