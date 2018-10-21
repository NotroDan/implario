package net.minecraft.client.gui.element;

import net.minecraft.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.Settings;
import net.minecraft.item.ItemStack;

public class IconButton extends SettingButton {

	private ItemStack item;
	private long hoverTime = 0;

	public IconButton(Settings settings, int x, int y, int widthIn, int heightIn, ItemStack item) {
		super(settings, x, y, widthIn, heightIn);
		this.item = item;
	}

	public IconButton(Settings settings, int x, int y, ItemStack item) {
		this(settings, x, y, 60, 60, item);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		Minecraft.getMinecraft().currentScreen.itemRender.renderItemIntoGUI(item, xPosition + 22, yPosition + 20, 1.5f);
		boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		if (this.hovered != hovered) {
			this.hovered = hovered;
			this.hoverTime = System.currentTimeMillis();
		}
		int i = 0xa0202020;
		long ago = System.currentTimeMillis() - hoverTime;
		if (hoverTime > 0) {
			if (ago < 250) {
				if (hovered) i = Utils.gradient(0xa07fff3f, 0xa0202020, ((float) (ago % 250)) / 250f);
				else i = Utils.gradient(0xa0202020, 0xa07fff3f, ((float) (ago % 250)) / 250f);
			}
			else if (hovered) i = 0xa07fff3f;
			else hoverTime = 0;
		}
		drawRect(xPosition, yPosition, xPosition + width, yPosition + height, i);
	}

}
