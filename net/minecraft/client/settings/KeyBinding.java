package net.minecraft.client.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.util.IntHashMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Set;

public enum KeyBinding implements Comparable<KeyBinding> {

	FORWARD("key.forward", 17, "key.categories.movement"),
	LEFT("key.left", 30, "key.categories.movement"),
	BACK("key.back", 31, "key.categories.movement"),
	RIGHT("key.right", 32, "key.categories.movement"),
	JUMP("key.jump", 57, "key.categories.movement"),
	SNEAK("key.sneak", 42, "key.categories.movement"),
	SPRINT("key.sprint", 29, "key.categories.movement"),

	INVENTORY("key.inventory", 18, "key.categories.inventory"),
	HOTBAR_1("key.hotbar.1", 2, "key.categories.inventory"),
	HOTBAR_2("key.hotbar.2", 3, "key.categories.inventory"),
	HOTBAR_3("key.hotbar.3", 4, "key.categories.inventory"),
	HOTBAR_4("key.hotbar.4", 5, "key.categories.inventory"),
	HOTBAR_5("key.hotbar.5", 6, "key.categories.inventory"),
	HOTBAR_6("key.hotbar.6", 7, "key.categories.inventory"),
	HOTBAR_7("key.hotbar.7", 8, "key.categories.inventory"),
	HOTBAR_8("key.hotbar.8", 9, "key.categories.inventory"),
	HOTBAR_9("key.hotbar.9", 10, "key.categories.inventory"),

	USE("key.use", -99, "key.categories.gameplay"),
	DROP("key.drop", 16, "key.categories.gameplay"),
	ATTACK("key.attack", -100, "key.categories.gameplay"),
	PICK("key.pickItem", -98, "key.categories.gameplay"),

	CHAT("key.chat", 20, "key.categories.multiplayer"),
	PLAYERLIST("key.playerlist", 15, "key.categories.multiplayer"),
	COMMAND("key.command", 53, "key.categories.multiplayer"),

	SCREENSHOT("key.screenshot", 60, "key.categories.misc"),
	PERSPECTIVE("key.togglePerspective", 63, "key.categories.misc"),
	SMOOTH_CAMERA("key.smoothCamera", 0, "key.categories.misc"),
	FULLSCREEN("key.fullscreen", 87, "key.categories.misc"),
	SPECTATOR_GLOW("key.spectatorOutlines", 0, "key.categories.misc"),
	ZOOM("of.key.zoom", 46, "key.categories.misc")
	;

	public static final KeyBinding[] HOTBAR = {HOTBAR_1, HOTBAR_2, HOTBAR_3, HOTBAR_4, HOTBAR_5, HOTBAR_6, HOTBAR_7, HOTBAR_8, HOTBAR_9};
	private static final List<KeyBinding> keybindArray = Lists.newArrayList();
	private static final IntHashMap<KeyBinding> hash = new IntHashMap();
	private static final Set<String> keybindSet = Sets.newHashSet();
	private final String keyDescription;
	private final int keyCodeDefault;
	private final String keyCategory;
	private int keyCode;
	
	static {
		for (KeyBinding key : values()) {
			keybindArray.add(key);
			hash.addKey(key.keyCode, key);
			keybindSet.add(key.getKeyCategory());
		}
	}

	/**
	 * Is the key held down?
	 */
	private boolean pressed;
	private int pressTime;

	public static void onTick(int keyCode) {
		if (keyCode != 0) {
			KeyBinding keybinding = hash.lookup(keyCode);

			if (keybinding != null) {
				++keybinding.pressTime;
			}
		}
	}

	public static void setKeyBindState(int keyCode, boolean pressed) {
		if (keyCode != 0) {
			KeyBinding keybinding = hash.lookup(keyCode);

			if (keybinding != null) {
				keybinding.pressed = pressed;
			}
		}
	}

	public static void unPressAllKeys() {
		for (KeyBinding keybinding : keybindArray) {
			keybinding.unpressKey();
		}
	}

	public static void resetKeyBindingArrayAndHash() {
		hash.clearMap();

		for (KeyBinding keybinding : keybindArray) {
			hash.addKey(keybinding.keyCode, keybinding);
		}
	}

	public static Set<String> getKeybinds() {
		return keybindSet;
	}

	KeyBinding(String description, int keyCode, String category) {
		this.keyDescription = description;
		this.keyCode = keyCode;
		this.keyCodeDefault = keyCode;
		this.keyCategory = category;
	}

	/**
	 * Returns true if the key is pressed (used for continuous querying). Should be used in tickers.
	 */
	public boolean isKeyDown() {
		return this.pressed;
	}

	public boolean isClicked() {
		int i = getKeyCode();
		return (i >= -100 && i <= 255) && (i != 0 && (i < 0 ? Mouse.isButtonDown(i + 100) : Keyboard.isKeyDown(i)));
	}

	public String getKeyCategory() {
		return this.keyCategory;
	}

	/**
	 * Returns true on the initial key press. For continuous querying use isKeyDown(). Should be used in key
	 * events.
	 */
	public boolean isPressed() {
		if (this.pressTime == 0) return false;
		--this.pressTime;
		return true;
	}

	private void unpressKey() {
		this.pressTime = 0;
		this.pressed = false;
	}

	public String getKeyDescription() {
		return this.keyDescription;
	}

	public int getKeyCodeDefault() {
		return this.keyCodeDefault;
	}

	public int getKeyCode() {
		return this.keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

}
