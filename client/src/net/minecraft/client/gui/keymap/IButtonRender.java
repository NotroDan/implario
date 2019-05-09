package net.minecraft.client.gui.keymap;

import net.minecraft.client.gui.Gui;

public interface IButtonRender {

	int DEFAULT_SIZE = 25;

	/**
	 * Рендерит контент на кнопке
	 */
	boolean render(Gui gui, KeyboardButton button, MetaKey metaKey);

}
