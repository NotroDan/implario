package net.minecraft.client.gui.element;

import java.util.List;

public interface RenderElement {

	static void render(List<RenderElement> list) {
		for (RenderElement element : list)
			element.render();
	}

	void render();

}
