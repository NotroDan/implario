package net.minecraft.client.gui;

import net.minecraft.client.settings.Settings;
import net.minecraft.client.sudoku.Cell;
import net.minecraft.client.sudoku.Sudoku;

import java.io.IOException;

public class GuiSudoku extends GuiScreen {

	private final Sudoku sudoku;

	public GuiSudoku() {
		this.sudoku = new Sudoku();
	}

	public int x() {
		return width / 2 - Sudoku.S * 17;
	}

	public int y() {
		return height / 2 - Sudoku.S * 17;
	}

	public Cell getCell(int mouseX, int mouseY) {

		int row = -1, column = -1;

//		for (int i = 0; i < Sudoku.S; i++) {
//			int ax = x() + y * (size + gap) + largeGap * (y / 3);
//
//		}

		int x = mouseX - x() + 5;
		int y = mouseY - y() + 5;
		int a = x * 3 / 103;
		int b = y * 3 / 103;
		System.out.println("x = " + a + ", y = " + b);
		return sudoku.getCell(a, b);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		sudoku.focused = getCell(mouseX, mouseY);
		System.out.println("Focused = " + sudoku.focused);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		Cell f = sudoku.focused;
		if (f != null && !f.isPreset()) {
			if (keyCode >= 2 && keyCode <= 11) {
				if (isCtrlKeyDown()) if (keyCode == 11) f.clearVariants(); else f.toggleVariant(keyCode - 1);
				else f.setValue(keyCode == 11 ? 0 : keyCode - 1);
			}
			if (keyCode == 14) {
				if (f.getValue() == 0) f.clearVariants();
				else f.setValue(0);
			}
		}
		switch (keyCode) {
			case 200:
				if (f != null && f.y > 0) sudoku.focused = sudoku.getCell(f.x, f.y - 1);
				break;
			case 203:
				if (f != null && f.x > 0) sudoku.focused = sudoku.getCell(f.x - 1, f.y);
				break;
			case 208:
				if (f != null && f.y + 1 < Sudoku.S) sudoku.focused = sudoku.getCell(f.x, f.y + 1);
				break;
			case 205:
				if (f != null && f.x + 1 < Sudoku.S) sudoku.focused = sudoku.getCell(f.x + 1, f.y);
				break;
			default:
				super.keyTyped(typedChar, keyCode);
				break;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++) sudoku.getCell(x, y).render(this, x(), y(), 3, 30, 4);
		if (Settings.SUDOKU_SEPARATORS.b()) for (int i = 0; i < Sudoku.B + 1; i++) {
			int a = (30 + 4) * i * Sudoku.B + i;
			drawVerticalLine(x() + a - 4, y() - 5, y() + 34 * Sudoku.S - 1, 0xc0d0d0d0);
			drawHorizontalLine(x() - 2, x() + 34 * Sudoku.S - 2,y() + a - 4, 0xc0d0d0d0);
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
