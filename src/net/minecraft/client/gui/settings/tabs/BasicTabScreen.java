package net.minecraft.client.gui.settings.tabs;

import net.minecraft.client.gui.settings.TabScreen;
import net.minecraft.client.gui.settings.tabs.element.Element;
import net.minecraft.client.renderer.G;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.client.gui.settings.GuiSettings.COLUMNWIDTH;

public class BasicTabScreen implements TabScreen {

	private static final int ELEMENTHEIGHT = 50;
	private final List<Element> elements = new ArrayList<>();
	private int lastReportedColumns;
	private int active;

	@Override
	public void render(int x, int y, float ticks, int width) {
		int columns = width / COLUMNWIDTH;
		if (columns < 1) columns = 1;
		lastReportedColumns = columns;

		int id = getHoverElement(x, y);
		Element hover = id == -1 ? null : elements.get(id);

		Iterator<Element> iterator = elements.iterator();
		c: while (iterator.hasNext()) {
			G.pushMatrix();
			for (int col = 0; col < columns; col++) {
				if (!iterator.hasNext()) {
					G.popMatrix();
					break c;
				}
				Element e = iterator.next();
				e.render(x, y, e == hover);
				G.translate(COLUMNWIDTH, 0, 0);
			}
			G.popMatrix();
			G.translate(0, ELEMENTHEIGHT, 0);
		}
	}

	public BasicTabScreen add(Element... elements) {
		this.elements.addAll(Arrays.asList(elements));
		return this;
	}

	public int getHoverElement(int mouseX, int mouseY) {
		if (mouseX < 0 || mouseY < 0) return -1;
		int columns = lastReportedColumns;
		int col = mouseX / COLUMNWIDTH;
		if (col < 0 || col > columns) return -1;
		int row = mouseY / ELEMENTHEIGHT;
		int e = row * columns + col;
		if (e < 0 || e >= elements.size()) return -1;
		return e;

	}

	@Override
	public void keyboard(int keycode, char c) {

	}

	@Override
	public void mouseDown(int mouseX, int mouseY, int mouseButton) {
//		System.out.println("click " + mouseX + " " + mouseY);

		int id = getHoverElement(mouseX, mouseY);
		if (id == -1) return;
		Element e = elements.get(id);
		if (e == null) return;
		active = id;
		e.mouseDown(mouseX - id % lastReportedColumns * COLUMNWIDTH, mouseY - id / lastReportedColumns * ELEMENTHEIGHT, mouseButton);

	}

	@Override
	public void mouseUp(int mx, int my, int button) {
		int id = getHoverElement(mx, my);
		if (id != active) {
			active = -1;
			return;
		}
		active = -1;
		if (id == -1) return;
		Element e = elements.get(id);
		if (e == null) return;
		e.mouseUp(mx - id % lastReportedColumns * COLUMNWIDTH, my - id / lastReportedColumns * ELEMENTHEIGHT, button);

	}

	@Override
	public void mouseDrag(int mx, int my, int button, long timeSinceLastClick) {

		if (active == -1) return;
//		int id = getHoverElement(mx, my);
//		if (id == -1) return;
//		Element e = elements.get(id);
//		if (e == null) return;
		elements.get(active).mouseDrag(mx - active % lastReportedColumns * COLUMNWIDTH,
				my - active / lastReportedColumns * ELEMENTHEIGHT, button, timeSinceLastClick);

	}

}
