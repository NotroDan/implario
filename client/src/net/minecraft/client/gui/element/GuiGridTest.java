package net.minecraft.client.gui.element;

import net.minecraft.client.MC;

public class GuiGridTest extends GuiGrid<String> {

	public GuiGridTest() {
		super(50, MC.FR.getFontHeight(), 10, 10, () -> new String[] {"Implario#2", "implario#1", "JOIJGDOFGO"});
	}

	@Override
	protected void drawForeground() {
		drawString(fontRendererObj, "GuiGridTest", width / 2, height / 2, -1);
	}

	@Override
	protected void drawElement(String element) {
		drawString(fontRendererObj, element, 0, 20, -1);
	}

	@Override
	protected void drawBackground() {
		drawDefaultBackground();
	}

}
