package net.minecraft.client.keystrokes;

import net.minecraft.client.settings.KeyBinding;

import java.util.HashSet;
import java.util.Set;

public class KeyStrokes {

	public static final Set<KeyStroke> strokes = new HashSet<>();

	public static void addKeyStroke(KeyBinding key, int x, int y, float size) {
		strokes.add(new KeyStroke(key, x, y, size));
	}

}
