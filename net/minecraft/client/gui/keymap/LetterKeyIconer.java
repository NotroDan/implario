package net.minecraft.client.gui.keymap;

import net.minecraft.client.MC;

public class LetterKeyIconer implements IKeyIconer {

	@Override
	public void render(Key key, MetaKey metaKey) {
		MC.FR.drawString(key.getCaption(), 0, 0, 0xeeeeee, false);
	}

}
