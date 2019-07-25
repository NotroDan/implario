package net.minecraft.client.settings;

import static net.minecraft.util.MathHelper.clamp_float;

public class SliderSetting extends Setting {

	private final float min;
	private float max;
	private final float defaultValue;
	public final float step;
	public float value;
	private final boolean percents, whole;

	public SliderSetting(String name, String caption, float min, float max, float defaultValue, float step) {
		super(name, caption);
		value = defaultValue;
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
		this.step = step;
		percents = max <= 1;
		whole = step == Math.floor(step);
	}

	public float getMax() {return max;}

	public float getMin() {return min;}

	@Override
	public void set(float value) {
		this.value = value;
	}

	@Override
	public float floatValue() {return value;}

	@Override
	public void set(String arg) {
		try {
			value = clamp_float(Float.parseFloat(arg), min, max);
		} catch (NumberFormatException ignored) {}
	}

	@Override
	public String toString() {
		return name + ": " + value;
	}

	@Override
	public void reset() {
		value = defaultValue;
	}


	public float normalizeValue(float value) {
		return clamp_float((this.snapToStepClamp(value) - min) / (max - min), 0.0F, 1.0F);
	}

	public float denormalizeValue(float value) {
		return this.snapToStepClamp(min + (max - min) * clamp_float(value, 0.0F, 1.0F));
	}

	public float snapToStepClamp(float value) {
		value = this.snapToStep(value);
		return clamp_float(value, min, max);
	}

	protected float snapToStep(float value) {
		if (step > 0.0F) value = step * (float) Math.round(value / step);
		return value;
	}


	public void setMax(float v) {
		this.max = v;
	}

	public String getCaption() {
		// ToDo: Кэш
		if (percents) return (int) (value * 100) + "%";
		if (whole) return String.valueOf((int) value);
		return String.valueOf(value);
	}

}
