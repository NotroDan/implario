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

	@Override
	public void render(int x, int y, float ticks, int width) {
		int columns = width / COLUMNWIDTH;
		lastReportedColumns = columns;
//		System.out.println(columns);
		if (columns < 1) columns = 1;

		Element hover = getHoverElement(x, y);

		Iterator<Element> iterator = elements.iterator();
		c: while (iterator.hasNext()) {
			G.pushMatrix();
			for (int col = 0; col < columns; col++) {
				if (!iterator.hasNext()) {
					G.popMatrix();
					break c;
				}
				Element e = iterator.next();
				e.render(x, y);
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

	public Element getHoverElement(int mouseX, int mouseY) {
		if (mouseX < 0 || mouseY < 0) return null;
		int columns = lastReportedColumns;
		int col = mouseX / COLUMNWIDTH;
		if (col < 0 || col > columns) return null;
		int row = mouseY / ELEMENTHEIGHT;
		int e = row * columns + col;
		if (e < 0 || e >= elements.size()) return null;
		return elements.get(e);

	}

	@Override
	public void keyboard(int keycode, char c) {

	}

	@Override
	public void mouseDown(int mouseX, int mouseY, int mouseButton) {
		System.out.println("click " + mouseX + " " + mouseY);

		Element e = getHoverElement(mouseX, mouseY);
		if (e == null) return;
		e.mouseDown(mouseX, mouseY, mouseButton);

	}

	@Override
	public void mouseUp(int mx, int my, int button) {

		Element e = getHoverElement(mx, my);
		if (e == null) return;
		e.mouseUp(mx, my, button);

	}

	@Override
	public void mouseDrag(int mx, int my, int button, long timeSinceLastClick) {
		Element e = getHoverElement(mx, my);
		if (e == null) return;
		e.mouseDrag(mx, my, button, timeSinceLastClick);

	}

}
