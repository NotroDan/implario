package net.minecraft.client.sudoku;

import net.minecraft.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Sudoku {

	public static final int B = 3;
	public static final int S = B * B;


	public Random random = new Random();
	public final Row[] rows = new Row[S];
	public final Column[] columns = new Column[S];
	public final Square[] squares = new Square[S];
	public volatile Cell focused;

	public Sudoku() {
		Cell[][] cells = new Cell[9][9];
		for (int x = 0; x < 9; x++) for (int y = 0; y < 9; y++) cells[x][y] = new Cell(x, y, this);
		focused = cells[0][0];
		for (int x = 0; x < 9; x++) columns[x] = Column.fromGrid(cells, x);
		for (int y = 0; y < 9; y++) rows[y] = Row.fromGrid(cells, y);
		for (int x = 0; x < 3; x++) for (int y = 0; y < 3; y++) squares[y * 3 + x] = Square.fromGrid(cells, x, y);
		System.out.println("Генерируем судоку...");
		long start = System.currentTimeMillis();
		read(Base.SUDOKUS[random.nextInt(Base.SUDOKUS.length)]);
		randomize(2000);
		unsolve(30);
		long end = System.currentTimeMillis();
		System.out.println("Генерация успешно завершена за " + (float) (end - start) + " мс.");
	}

	private void read(String s) {
		char[] a = s.toCharArray();
		for (int x = 0; x < S; x++) for (int y = 0; y < S; y++) getCell(x, y).setValue(Integer.parseInt(String.valueOf(a[x * S + y])));
	}

	private void baseGrid() {
		Integer[] row = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
		for (int x = 0; x < S; x++) {
			for (int y = 0; y < row.length; y++) getCell(x, y).setValue(row[y]);
			Utils.cyclicShift(row, x % B == B - 1 ? B + 1 : B);
		}
	}

	public void transpose() {
		int[][] o = getValues();
		for (int x = 0; x < S; x++) for (int y = 0; y < S; y++) getCell(x, y).setValue(o[y][x]);
	}

	public void swapColumns(int x1, int x2) {
		for (int y = 0; y < S; y++) {
			Cell c1 = getCell(x1, y);
			Cell c2 = getCell(x2, y);
			int a = c1.value;
			c1.setValue(c2.value);
			c2.setValue(a);
		}
	}
	public void swapRows(int y1, int y2) {
		for (int x = 0; x < S; x++) {
			Cell c1 = getCell(x, y1);
			Cell c2 = getCell(x, y2);
			int a = c1.value;
			c1.setValue(c2.value);
			c2.setValue(a);
		}
	}
	public void swapPillars(int x1, int x2) {
		for (int i = 0; i < 3; i++) swapColumns(x1 * 3 + i, x2 * 3 + i);
	}
	public void swapRacks(int y1, int y2) {
		for (int i = 0; i < 3; i++) swapRows(y1 * 3 + i, y2 * 3 + i);
	}

	public int[][] getValues() {
		int[][] values = new int[S][S];
		for (int x = 0; x < S; x++) {
			Cell[] c = columns[x].cells;
			for (int i = 0; i < c.length; i++) values[x][i] = c[i].value;
		}
		return values;
	}

	public Cell getCell(int x, int y) {
		try {
			return columns[x].cells[y];
		} catch (ArrayIndexOutOfBoundsException ex) {
			return null;
		}
	}

	public void removeVariants() {
		List<Integer> list = new ArrayList<>();
		for (Row row : rows) {
			for (Cell cell : row.cells) if (cell.value != 0) list.add(cell.value);
			for (Cell cell : row.cells) if (cell.value == 0) for (Integer i : list) if (i != null) cell.variants[i - 1] = false;
			list.clear();
		}
		for (Square row : squares) {
			for (Cell cell : row.cells) if (cell.value != 0) list.add(cell.value);
			for (Cell cell : row.cells) if (cell.value == 0) for (Integer i : list) if (i != null) cell.variants[i - 1] = false;
			list.clear();
		}
		for (Column row : columns) {
			for (Cell cell : row.cells) if (cell.value != 0) list.add(cell.value);
			for (Cell cell : row.cells) if (cell.value == 0) for (Integer i : list) if (i != null) cell.variants[i - 1] = false;
			list.clear();
		}
	}

	public static abstract class Group {
		protected final Cell[] cells;
		protected Group(Cell[] cells) {
			this.cells = cells;
		}
	}

	public void fish() {
		for (int x = 0; x < 9; x++)
			for (int y = 0; y < 9; y++) {
				Cell c = getCell(x, y);
				double d = Math.random();
				if (d < 0.33) continue;
				if (d > 0.67) {
					c.value = (int) (Math.random() * 9) + 1;
					c.preset = true;
				}
				else {
					int f = (int) (Math.random() * 3 + 3);
					for (int i = 0; i < f; i++) c.variants[(int) (Math.random() * c.variants.length)] = true;
				}
			}

	}

	public void updateVariants() {
		for (Row row : rows) {
			for (Cell cell : row.cells) {
				for (int i = 0; i < 9; i++) {
					cell.variants[i] = true;
				}
			}
		}
		removeVariants();
	}

	public void unsolve(int opened) {
		int[] numbers = IntStream.range(0, S * S - 1).toArray();
		System.out.println(numbers.length);
		Utils.shuffleArray(numbers);
		System.out.println(numbers.length);
		List<Integer> list = new ArrayList<>(opened);
		for (int i = 0; i < opened; i++) list.add(numbers[i]);
		for (int x = 0; x < S; x++) for (int y = 0; y < S; y++) {
			if (!list.contains(x * S + y)) getCell(x, y).setValue(0);
			else getCell(x, y).preset = true;
		}
	}

	public void randomize(int iterations) {
		boolean transposed = false;
		for (int i = 0; i < iterations; i++) {
			int r = random.nextInt(5);
			if (r == 0 && transposed) {
				i--;
				continue;
			}
			transposed = i == 0;
			switch (r) {
				case 0:
					transpose();
					break;
				case 1:
					int pillar = random.nextInt(B);
					swapColumns(pillar * B + random.nextInt(B), pillar * B + random.nextInt(B));
					break;
				case 2:
					int rack = random.nextInt(B);
					swapRows(rack * B + random.nextInt(B), rack * B + random.nextInt(B));
					break;
				case 3:
					swapRacks(random.nextInt(B), random.nextInt(B));
					break;
				case 4:
					swapPillars(random.nextInt(B), random.nextInt(B));
					break;
			}
		}
	}

}
