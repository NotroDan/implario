package net.minecraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public class TabSet {

	public final List<Tab> tabs = new ArrayList<>();
	public int current = -1;
	public int y;

	public TabSet(int y) {
		this.y = y;
	}

	public void reset() {
		for (Tab tab : tabs) tab.getButtons().clear();
		tabs.clear();
	}

	public void add(Tab tab) {
		tabs.add(tab);
	}

	public Tab add(String title, GuiButton... btns) {
		FontRenderer r = Minecraft.getMinecraft().fontRendererObj;
		Tab tab = new Tab(title, 1000 + (tabs.isEmpty() ? 0 : tabs.size()), 0, y);
		tab.add(btns);
		tabs.add(tab);
		return tab;
	}

	public void recountPositions(int screenWidth) {
		int w = tabWidth();
		w = screenWidth / 2 - w / 2;
		for (Tab tab : tabs) {
			tab.getButton().xPosition = w;
			w += tab.getButton().width;
		}
	}

	public void init(List<GuiButton> baseList, int width) {
		for (Tab tab : tabs) tab.addTo(baseList);
		recountPositions(width);
	}

	public void select(int i) {
		if (current >= 0) tabs.get(current).unfocus();
		tabs.get(current = i).focus();
	}

	public int tabWidth() {
		int w = 0;
		for (Tab tab : tabs) w += tab.getButton().width;
		return w;
	}

}
