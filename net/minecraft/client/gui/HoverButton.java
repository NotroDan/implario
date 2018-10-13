package net.minecraft.client.gui;

import com.google.common.collect.Lists;

import java.util.List;

public class HoverButton extends GuiButton {
	private final List<String> hoverText;

	public HoverButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, String... hoverText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.hoverText = Lists.newArrayList(hoverText);
	}

	public List<String> getHoverText() {
		return hoverText;
	}

}
