package net.minecraft.client.gui.keymap;

public class KeyboardButton {

	private final int width, height;
	private final Key key;
	private final int x, y;
	private final int renderY;

	public KeyboardButton(int width, int height, Key key, int x, int y) {
		this(width, height, key, x, y, 0);
	}

	public KeyboardButton(int width, int height, Key key, int x, int y, int renderY) {
		this.width = width;
		this.height = height;
		this.key = key;
		this.x = x;
		this.y = y;
		this.renderY = renderY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public Key getKey() {
		return key;
	}

	@Override
	public int hashCode() {
		return key.ordinal();
	}

	public int getRenderY() {
		return renderY;
	}

}
