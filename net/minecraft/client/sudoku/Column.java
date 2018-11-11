package net.minecraft.client.sudoku;

public class Column extends Sudoku.Group {

	protected Column(Cell[] cells) {
		super(cells);
	}

	public static Column fromGrid(Cell[][] cells, int x) {
		return new Column(cells[x]);
	}

}
