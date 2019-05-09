package net.minecraft.client.gui.keymap;

import java.util.HashMap;
import java.util.Map;

public enum KeyBinding {

	FORWARD("Вперёд", Key.W),
	BACKWARD("Назад", Key.S),
	LEFT("Влево", Key.A),
	RIGHT("Вправо", Key.D),
	JUMP("Прыжок", Key.SPACE),
	SCREENSHOT("Скриншот", Key.F2),
	FULLSCREEN("Полный экран", Key.F11),
	SPRINT("Бег", Key.LCONTROL),
	ZOOM("Приблизить", Key.R),

	;

	public static final Map<Key, KeyBinding> KEYMAP = new HashMap<>();
	static {
		for (KeyBinding b : values()) KEYMAP.put(b.key, b);
	}

	private final String name;
	private final Key defaultKey;
	private Key key;

	KeyBinding(String name, Key defaultKey) {
		this.name = name;
		this.defaultKey = defaultKey;
		this.key = defaultKey;
	}

	public String getName() {
		return name;
	}

	public Key getDefaultKey() {
		return defaultKey;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}
}
