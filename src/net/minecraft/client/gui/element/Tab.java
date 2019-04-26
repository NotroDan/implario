package net.minecraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.AssetsFontRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tab implements ITab{

	private final List<GuiButton> buttons = new ArrayList<>();
	public final String title;
	private final GuiButton button;
	private boolean focused = false;

	public Tab(String title, int id, int x, int y) {
		this.title = title;
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

	@Override
	public List<GuiButton> getButtons() {
		return buttons;
	}

	@Override
	public GuiButton getButton() {
		return button;
	}

	@Override
	public void focus() {
		focused = true;
		button.enabled = false;
		for (GuiButton button : buttons) button.visible = true;
	}

	@Override
	public void unfocus() {
		focused = false;
		button.enabled = true;
		for (GuiButton button : buttons) button.visible = false;
	}

	@Override
	public void addTo(List<GuiButton> buttonList) {
		buttonList.add(button);
		buttonList.addAll(buttons);
	}
}
