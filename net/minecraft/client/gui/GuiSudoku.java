package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.sudoku.Cell;
import net.minecraft.client.sudoku.Sudoku;

import java.io.IOException;
import java.util.Random;

import static net.minecraft.client.sudoku.Sudoku.B;
import static net.minecraft.client.sudoku.Sudoku.S;

public class GuiSudoku extends GuiScreen {

	private final Sudoku sudoku;
	public final Random random;
	public volatile int highlightVar;
	public GuiButton highlightBtn;

	public GuiSudoku() {
		this.sudoku = new Sudoku();
		random = sudoku.random;
	}

	@Override
	public void initGui() {
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 + Sudoku.S * 17 + 10, height / 2 - 30, 50, 20, "transpose"));
		buttonList.add(new GuiButton(1, width / 2 + Sudoku.S * 17 + 10, height / 2 - 5, 50, 20, "colswap"));
		buttonList.add(new GuiButton(2, width / 2 + Sudoku.S * 17 + 10, height / 2 + 20, 50, 20, "rowswap"));
		buttonList.add(new GuiButton(3, width / 2 + Sudoku.S * 17 + 10, height / 2 + 45, 50, 20, "pillswap"));
		buttonList.add(new GuiButton(4, width / 2 + Sudoku.S * 17 + 10, height / 2 + 70, 50, 20, "rackswap"));
		buttonList.add(new GuiButton(5, width / 2 + Sudoku.S * 17 + 10, height / 2 + 95, 50, 20, "sumovars"));
		for (int i = S - 1; i >= 0; i--)
			buttonList.add(new GuiButton(S - i + 6, width / 2 - (i - S / 2) * 20 - 10, height / 2 - S * 17 - 30, 20, 20, String.valueOf(S - i)) {
				@Override
				public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
					return this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				}
			});
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
		if (x < 0 || y < 0) return null;
		int a = x * 3 / 103;
		int b = y * 3 / 103;
		return sudoku.getCell(a, b);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		Cell cell = getCell(mouseX, mouseY);
		if (cell != null) sudoku.focused = cell;
		else super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		Cell f = sudoku.focused;
		if (f != null && !f.isPreset()) {
			if (keyCode >= 2 && keyCode <= 11) {
				if (isCtrlKeyDown()) if (keyCode == 11) f.clearVariants(); else f.toggleVariant(keyCode - 1);
				else {
					f.setValue(keyCode == 11 ? 0 : keyCode - 1);
					sudoku.removeVariants();
				}
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
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id >= 6 && button.id <= 15) {
			int i = button.id - 6;
			highlightVar = highlightVar == i ? 0 : i;
			if (highlightBtn != null) {
				highlightBtn.enabled = true;
				highlightBtn.height = 20;
			}
			if (highlightVar != 0) {
				(highlightBtn = button).enabled = false;
				button.height = 24;
			}
		}
		switch (button.id) {
			case 0:
				sudoku.transpose();
				break;
			case 1:
				int pillar = random.nextInt(B);
				sudoku.swapColumns(pillar * B + random.nextInt(B), pillar * B + random.nextInt(B));
				break;
			case 2:
				int rack = random.nextInt(B);
				sudoku.swapRows(rack * B + random.nextInt(B), rack * B + random.nextInt(B));
				break;
			case 3:
				sudoku.swapRacks(random.nextInt(B), random.nextInt(B));
				break;
			case 4:
				sudoku.swapPillars(random.nextInt(B), random.nextInt(B));
				break;
			case 5:
				sudoku.updateVariants();
				break;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++) sudoku.getCell(x, y).render(this, x(), y(), 3, 30, 4, sudoku.focused, highlightVar);
		if (Settings.SUDOKU_SEPARATORS.b()) for (int i = 0; i < B + 1; i++) {
			int a = (30 + 4) * i * B + i;
			drawVerticalLine(x() + a - 4, y() - 5, y() + 34 * Sudoku.S - 1, 0xc0d0d0d0);
			drawHorizontalLine(x() - 2, x() + 34 * Sudoku.S - 2,y() + a - 4, 0xc0d0d0d0);
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
