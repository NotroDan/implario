package net.minecraft.client.sudoku;

public class Row extends Sudoku.Group {

	protected Row(Cell[] cells) {
		super(cells);
	}

	public static Row fromGrid(Cell[][] cells, int y) {
		Cell[] r = new Cell[9];
		for (int x = 0; x < 9; x++) r[x] = cells[x][y];
		return new Row(r);
	}

}
