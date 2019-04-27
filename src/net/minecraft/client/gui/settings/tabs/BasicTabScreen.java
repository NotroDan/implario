package net.minecraft.client.gui.settings.tabs;

import net.minecraft.client.gui.settings.TabScreen;
import net.minecraft.client.gui.settings.tabs.element.Element;
import net.minecraft.client.renderer.G;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.client.gui.settings.GuiSettings.COLUMNHEIGHT;

public class BasicTabScreen implements TabScreen {

	private static final int ELEMENTHEIGHT = 50;
	private final List<Element> elements = new ArrayList<>();

	@Override
	public void render(float mx, float my, float ticks, int width) {
		int columns = width % COLUMNHEIGHT;
		if (columns < 1) columns = 1;

		Iterator<Element> iterator = elements.iterator();
		c: while (iterator.hasNext()) {
			G.pushMatrix();
			for (int col = 0; col < columns; col++) {
				if (!iterator.hasNext()) {
					G.popMatrix();
					break c;
				}
				Element e = iterator.next();
				e.render();
				G.translate(COLUMNHEIGHT, 0, 0);
			}
			G.popMatrix();
			G.translate(0, ELEMENTHEIGHT, 0);
		}
	}

	public BasicTabScreen add(Element... elements) {
		this.elements.addAll(Arrays.asList(elements));
		return this;
	}

	@Override
	public void keyboard(int keycode, char c) {

	}

}
