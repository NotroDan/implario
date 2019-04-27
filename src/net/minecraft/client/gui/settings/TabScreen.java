package net.minecraft.client.gui.settings;

public interface TabScreen {

	void render(float mx, float my, float ticks, int width);
	void keyboard(int keycode, char c);

}
