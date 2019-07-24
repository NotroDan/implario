package net.minecraft.util;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.G;
import org.lwjgl.opengl.GL14;

public class JsonBlendingMode {

	private static JsonBlendingMode instance = null;
	private final int field_148116_b;
	private final int field_148117_c;
	private final int field_148114_d;
	private final int field_148115_e;
	private final int field_148112_f;
	private final boolean field_148113_g;
	private final boolean field_148119_h;

	private JsonBlendingMode(boolean p_i45084_1_, boolean p_i45084_2_, int p_i45084_3_, int p_i45084_4_, int p_i45084_5_, int p_i45084_6_, int p_i45084_7_) {
		this.field_148113_g = p_i45084_1_;
		this.field_148116_b = p_i45084_3_;
		this.field_148114_d = p_i45084_4_;
		this.field_148117_c = p_i45084_5_;
		this.field_148115_e = p_i45084_6_;
		this.field_148119_h = p_i45084_2_;
		this.field_148112_f = p_i45084_7_;
	}

	public JsonBlendingMode() {
		this(false, true, 1, 0, 1, 0, 32774);
	}

	public JsonBlendingMode(int p_i45085_1_, int p_i45085_2_, int p_i45085_3_) {
		this(false, false, p_i45085_1_, p_i45085_2_, p_i45085_1_, p_i45085_2_, p_i45085_3_);
	}

	public JsonBlendingMode(int p_i45086_1_, int p_i45086_2_, int p_i45086_3_, int p_i45086_4_, int p_i45086_5_) {
		this(true, false, p_i45086_1_, p_i45086_2_, p_i45086_3_, p_i45086_4_, p_i45086_5_);
	}

	public static JsonBlendingMode deserialize(JsonObject json) {
		if (json == null) return new JsonBlendingMode();
		int func = 32774;
		int j = 1;
		int k = 0;
		int l = 1;
		int i1 = 0;
		boolean flag = true;
		boolean flag1 = false;

		if (JsonUtils.isString(json, "func")) {
			func = getBlendingFunc(json.get("func").getAsString());
			if (func != 32774) flag = false;
		}

		if (JsonUtils.isString(json, "srcrgb")) {
			j = func_148107_b(json.get("srcrgb").getAsString());
			if (j != 1) flag = false;
		}

		if (JsonUtils.isString(json, "dstrgb")) {
			k = func_148107_b(json.get("dstrgb").getAsString());

			if (k != 0) {
				flag = false;
			}
		}

		if (JsonUtils.isString(json, "srcalpha")) {
			l = func_148107_b(json.get("srcalpha").getAsString());

			if (l != 1) {
				flag = false;
			}

			flag1 = true;
		}

		if (JsonUtils.isString(json, "dstalpha")) {
			i1 = func_148107_b(json.get("dstalpha").getAsString());

			if (i1 != 0) {
				flag = false;
			}

			flag1 = true;
		}

		return flag ? new JsonBlendingMode() : flag1 ? new JsonBlendingMode(j, k, l, i1, func) : new JsonBlendingMode(j, k, func);
	}

	private static int getBlendingFunc(String value) {
		String s = value.trim().toLowerCase();
		return s.equals("add") ? 32774 :
				s.equals("subtract") ? 32778 :
						s.equals("reversesubtract") ? 32779 :
								s.equals("reverse_subtract") ? 32779 :
										s.equals("min") ? 32775 :
												s.equals("max") ? 32776 : 32774;
	}

	private static int func_148107_b(String p_148107_0_) {
		String s = p_148107_0_.trim().toLowerCase();
		s = s.replaceAll("_", "");
		s = s.replaceAll("one", "1");
		s = s.replaceAll("zero", "0");
		s = s.replaceAll("minus", "-");
		return s.equals("0") ? 0 : s.equals("1") ? 1 : s.equals("srccolor") ? 768 : s.equals("1-srccolor") ? 769 : s.equals("dstcolor") ? 774 : s.equals("1-dstcolor") ? 775 : s.equals(
				"srcalpha") ? 770 : s.equals("1-srcalpha") ? 771 : s.equals("dstalpha") ? 772 : s.equals("1-dstalpha") ? 773 : -1;
	}

	public void func_148109_a() {
		if (this.equals(instance)) return;
		if (instance == null || this.field_148119_h != instance.func_148111_b()) {
			instance = this;

			if (this.field_148119_h) {
				G.disableBlend();
				return;
			}

			G.enableBlend();
		}

		GL14.glBlendEquation(this.field_148112_f);

		if (this.field_148113_g) {
			G.tryBlendFuncSeparate(this.field_148116_b, this.field_148114_d, this.field_148117_c, this.field_148115_e);
		} else {
			G.blendFunc(this.field_148116_b, this.field_148114_d);
		}
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JsonBlendingMode)) return false;
		JsonBlendingMode m = (JsonBlendingMode) o;
		return this.field_148112_f == m.field_148112_f &&
				this.field_148115_e == m.field_148115_e &&
				this.field_148114_d == m.field_148114_d &&
				this.field_148119_h == m.field_148119_h &&
				this.field_148113_g == m.field_148113_g &&
				this.field_148117_c == m.field_148117_c &&
				this.field_148116_b == m.field_148116_b;
	}

	public int hashCode() {
		int i = this.field_148116_b;
		i = 31 * i + this.field_148117_c;
		i = 31 * i + this.field_148114_d;
		i = 31 * i + this.field_148115_e;
		i = 31 * i + this.field_148112_f;
		i = 31 * i + (this.field_148113_g ? 1 : 0);
		i = 31 * i + (this.field_148119_h ? 1 : 0);
		return i;
	}

	public boolean func_148111_b() {
		return this.field_148119_h;
	}

}
