package net.minecraft.client.settings;

import net.minecraft.util.MathHelper;

public class SelectorSetting extends Setting {

	public final String[] titles;
	public int state;
	private int defaultState;

	public SelectorSetting(String name, String caption, int defaultState, String... titles) {
		super(name, caption);
		this.titles = titles;
		this.defaultState = defaultState;
		state = defaultState >= titles.length ? 0 : defaultState;
	}

	public int toggle() {
		return ++state >= titles.length ? state = 0 : state;
	}

	@Override
	public void set(float f) {
		int state = ((int) f);
		state = MathHelper.clamp_int(state, 0, titles.length - 1);
		this.state = state;
	}

	@Override
	public String toString() {
		return name + ": " + state;
	}

	@Override
	public float floatValue() {
		return super.floatValue();
	}

	@Override
	public void set(String arg) {
		try {
			state = MathHelper.clamp_int(Integer.parseInt(arg), 0, titles.length - 1);
		} catch (NumberFormatException ignored) {}
	}

	@Override
	public void reset() {
		state = defaultState;
	}

}
