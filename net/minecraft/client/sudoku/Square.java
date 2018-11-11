package net.minecraft.client.sudoku;

public class Square extends Sudoku.Group {

	protected Square(Cell[] cells) {
		super(cells);
	}


	public static Square fromGrid(Cell[][] cells, int sx, int sy) {
		Cell[] r = new Cell[9];
		sx *= 3;
		sy *= 3;
		int p = 0;
		for (int x = sx; x < sx + 3; x++)
			for (int y = sy; y < sy + 3; y++)
				r[p++] = cells[x][y];
		return new Square(r);
	}

}
