package net.minecraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.AssetsFontRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tab {

	private final List<GuiButton> buttons = new ArrayList<>();
	public final String title;
	private final GuiButton button;
	private boolean focused = false;
	private final int id;

	public Tab(String title, int id, int x, int y) {
		this.title = title;
		this.id = id;
		AssetsFontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		button = new GuiButton(id, x, y, fr.getStringWidth(title) + 12, 18, title);
	}

	public Tab(String title, int id, Tab last) {
		this(title, id, last.button.xPosition + last.button.width, last.button.yPosition);
	}

	public void add(GuiButton... buttons) {
		this.buttons.addAll(Arrays.asList(buttons));
		for (GuiButton button : buttons) button.visible = this.focused;
	}

	public List<GuiButton> getButtons() {
		return buttons;
	}

	public GuiButton getButton() {
		return button;
	}

	public String getTitle() {
		return title;
	}

	public int getId() {
		return id;
	}

	public void focus() {
		focused = true;
		button.enabled = false;
		for (GuiButton button : buttons) button.visible = true;
	}

	public void unfocus() {
		focused = false;
		button.enabled = true;
		for (GuiButton button : buttons) button.visible = false;
	}

	public void addTo(List<GuiButton> buttonList) {
		buttonList.add(button);
		buttonList.addAll(buttons);
	}

}
