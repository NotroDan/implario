package net.minecraft.client.gui.element;

import java.util.ArrayList;
import java.util.List;

public class TabSet {

	public final List<ITab> tabs = new ArrayList<>();
	public int current = -1;
	public int y;

	public TabSet(int y) {
		this.y = y;
	}

	public void reset() {
		for (ITab tab : tabs) tab.getButtons().clear();
		tabs.clear();
	}

	public void add(ITab tab) {
		tabs.add(tab);
	}

	public ITab add(String title, Runnable runnable){
		ITab tab = new RunnableTab(title, runnable, 1000 + (tabs.isEmpty() ? 0 : tabs.size()), 0, y);
		tabs.add(tab);
		return tab;
	}

	public ITab add(String title, GuiButton... btns) {
		ITab tab = new Tab(title, 1000 + (tabs.isEmpty() ? 0 : tabs.size()), 0, y);
		tab.add(btns);
		tabs.add(tab);
		return tab;
	}

	public void recountPositions(int screenWidth) {
		int w = tabWidth();
		w = screenWidth / 2 - w / 2;
		for (ITab tab : tabs) {
			tab.getButton().xPosition = w;
			w += tab.getButton().width;
		}
	}

	public void init(List<GuiButton> baseList, int width) {
		for (ITab tab : tabs) tab.addTo(baseList);
		recountPositions(width);
	}

	public void select(int i) {
		if (current >= 0) tabs.get(current).unfocus();
		tabs.get(current = i).focus();
	}

	public int tabWidth() {
		int w = 0;
		for (ITab tab : tabs) w += tab.getButton().width;
		return w;
	}

	public int getCurrent() {
		return current;
	}

	public List<ITab> getTabs() {
		return tabs;
	}

}
