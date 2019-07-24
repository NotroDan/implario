package net.minecraft.client.settings;

public class ToggleSetting extends Setting {


	private final boolean defaultValue;
	public boolean value;

	public ToggleSetting(String name, String caption, boolean defaultValue) {
		super(name, caption);
		this.value = defaultValue;
		this.defaultValue = defaultValue;
	}

	public boolean toggle() {
		return value = !value;
	}

	@Override
	public boolean booleanValue() {return value;}

	@Override
	public void set(boolean b) {value = b;}

	@Override
	public void set(String arg) {
		value = "true".equals(arg);
	}

	@Override
	public void reset() {
		value = defaultValue;
	}

	@Override
	public String toString() {
		return name + ": " + value;
	}

}
