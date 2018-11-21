package net.minecraft.item;

import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Group {

	private final List<Unit> elements = new ArrayList<>();
	private final int width, height;
	private final String name;

	public Group(String name, int width, int height, Block... blocks) {
		this(name, width, height, toUnitArray(blocks));
	}

	public Group(String name, int width, int height, String... items) {
		this(name, width, height, toUnitArray(items));
	}

	private static Unit[] toUnitArray(String[] items) {
		List<Unit> list = new ArrayList<>();
		for (String s : items) {
			String[] a = s.split(":");
			Item item = Item.itemRegistry.getObjectById(Integer.parseInt(a[0]));
			ItemStack i;
			if (a.length > 1) i = new ItemStack(item, Integer.parseInt(a[1]));
			else i = new ItemStack(item);
			list.add(new Unit(i));
		}
		return list.toArray(new Unit[0]);
	}

	private static Unit[] toUnitArray(Block[] blocks) {
		Unit[] u = new Unit[blocks.length];
		for (int i = 0; i < blocks.length; i++) {
			u[i] = new Unit(new ItemStack(blocks[i]));
		}
		return u;
	}

	public Group(String name, int width, int height, Unit... elements) {
		this.width = width;
		this.height = height;
		this.name = name;
		add(elements);
	}

	public void add(Unit... elements) {
		this.elements.addAll(Arrays.asList(elements));
	}
	public void add(Collection<Unit> elements) {
		this.elements.addAll(elements);
	}

	public String getName() {
		return name;
	}
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	public List<Unit> getElements() {
		return elements;
	}


	public static class Unit {

		public ItemStack item;
		public Unit[] alternative;

		public Unit(ItemStack item, Unit... alternative) {
			this.item = item;
			this.alternative = alternative;
		}

	}

}
