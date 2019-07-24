package net.minecraft.client.gui.keymap;

public enum MetaKey {

	NONE, CTRL, SHIFT, ALT, CTRL_ALT, SHIFT_ALT, CTRL_SHIFT, CTRL_SHIFT_ALT;

	public static MetaKey get(boolean ctrl, boolean shift, boolean alt) {
		if (ctrl) {
			if (shift) return alt ? CTRL_SHIFT_ALT : CTRL_SHIFT;
			return alt ? CTRL_ALT : CTRL;
		}
		if (shift) return alt ? SHIFT_ALT : SHIFT;
		return alt ? ALT : NONE;
	}

}
