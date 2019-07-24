package net.minecraft.client.gui.settings;

public interface TabScreen {

	void render(int mx, int my, float ticks, int width);

	void keyboard(int keycode, char c);

	default void mouseDown(int mouseX, int mouseY, int mouseButton) {}

	default void mouseUp(int mx, int my, int button) {}

	default void mouseDrag(int mx, int my, int button, long timeSinceLastClick) {}

}
