package net.minecraft.client.resources.model;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class ModelResourceLocation extends ResourceLocation {

	private final String variant;

	protected ModelResourceLocation(String... p_i46078_2_) {
		super(p_i46078_2_[0], p_i46078_2_[1]);
		this.variant = StringUtils.isEmpty(p_i46078_2_[2]) ? "normal" : p_i46078_2_[2].toLowerCase();
	}

	public ModelResourceLocation(String path) {
		this(parsePathString(path));
	}

	public ModelResourceLocation(ResourceLocation p_i46080_1_, String p_i46080_2_) {
		this(p_i46080_1_.toString(), p_i46080_2_);
	}

	public ModelResourceLocation(String p_i46081_1_, String p_i46081_2_) {
		this(parsePathString(p_i46081_1_ + '#' + (p_i46081_2_ == null ? "normal" : p_i46081_2_)));
	}

	protected static String[] parsePathString(String input) {
		String[] split = new String[] {null, input, null};
		int i = input.indexOf(35);
		String s = input;

		if (i >= 0) {
			split[2] = input.substring(i + 1);
			if (i > 1) s = input.substring(0, i);
		}

		System.arraycopy(ResourceLocation.splitObjectName(s), 0, split, 0, 2);
		return split;
	}

	public String getVariant() {
		return this.variant;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		}
		if (p_equals_1_ instanceof ModelResourceLocation && super.equals(p_equals_1_)) {
			ModelResourceLocation modelresourcelocation = (ModelResourceLocation) p_equals_1_;
			return this.variant.equals(modelresourcelocation.variant);
		}
		return false;
	}

	public int hashCode() {
		return 31 * super.hashCode() + this.variant.hashCode();
	}

	public String toString() {
		return super.toString() + '#' + this.variant;
	}

}
