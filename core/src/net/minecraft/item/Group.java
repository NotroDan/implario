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

	public Group(String name, int width, int height, String... items) {
		this(name, width, height, toUnitArray(items));
	}

	private static Unit[] toUnitArray(String[] items) {
		List<Unit> list = new ArrayList<>();
		for (String s : items) {
			if (s == null || s.length() == 0 || s.equals("0")) {
				list.add(new Unit(null));
				continue;
			}
			String[] a = s.split(":");
			int data = a.length > 1 ? Integer.parseInt(a[1]) : 0;
			ItemStack i = createItemStack(Integer.parseInt(a[0]), data);
			list.add(new Unit(i));
		}
		return list.toArray(new Unit[0]);
	}

	public Group(String name, int width, int height, Unit... elements) {
		this.width = width;
		this.height = height;
		this.name = name;
		add(elements);
	}

	public static Unit[] every(int id, int amount) {
		Unit[] u = new Unit[amount];
		for (int i = 0; i < amount; i++) {
			u[i] = new Unit(createItemStack(id, i));
		}
		return u;
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

		public ItemStack getItem() {
			return item;
		}

	}

	public static ItemStack createItemStack(int id, int data) {
		try {
			Item i = Item.getItemById(id);
			return new ItemStack(i, 1, data);
		} catch (NullPointerException e) {
			Block b = Block.getBlockById(id);
			return new ItemStack(b, 1, data);
		}
	}
}
