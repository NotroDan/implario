package net.minecraft.client.gui.element;

import java.util.List;

public interface RenderElement {

	void render();

	static void render(List<RenderElement> list) {
		for (RenderElement element : list)
			element.render();
	}

}
