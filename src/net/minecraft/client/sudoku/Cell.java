package net.minecraft.client.sudoku;

import net.minecraft.client.MC;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.G;

public class Cell {

	protected final boolean[] variants = new boolean[9];
	protected int value;
	protected boolean preset;
	public final int x, y;
	private final Sudoku parent;

	public Cell(int x, int y, Sudoku parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	public void render(GuiScreen gui, int baseX, int baseY, int gap, int size, int largeGap, Cell focused, int highlightVar) {
		int ax = baseX + x * (size + gap) + largeGap * (x / 3);
		int ay = baseY + y * (size + gap) + largeGap * (y / 3);
		int color = 0xc0302020;
		if (value == 0 && highlightVar != 0 && variants[highlightVar - 1]) color = 0x408ee9f9;
		if (focused.getValue() == value && value != 0) color = 0xc0504040;
		if (parent.focused == this) color = 0xc0a0a020;
		Gui.drawRect(ax, ay, ax + size, ay + size, color);
		if (value == 0) {
			int w = size / 3;
			for (int v = 0; v < variants.length; v++) {
				if (!variants[v]) continue;
				gui.drawCenteredString(MC.getFontRenderer(), String.valueOf(v + 1), ax + w * (v % 3) + 5, ay + w * (v / 3), 0xffa0a0a0);
			}
		} else {
			G.scale(4, 4, 4);
			gui.drawCenteredString(MC.getFontRenderer(), String.valueOf(value), (int) ((float) ax / 4 + (float) size / 8f + 1f), ay / 4, preset ? -1 : 0x30ee30);
			G.scale(0.25, 0.25, 0.25);
		}
	}

	@Override
	public String toString() {
		return "{x-" + x + ", y-" + y + ", value-" + value + ", " + variants.length + " vars}";
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void clearVariants() {
		for (int i = 0; i < 9; i++) variants[i] = false;
	}

	public boolean isPreset() {
		return preset;
	}

	public void toggleVariant(int i) {
		variants[i - 1] = !variants[i - 1];
	}

	public int getValue() {
		return value;
	}

}
