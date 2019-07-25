package optifine;

import net.minecraft.client.renderer.G;

public class Blender {

	public static final int BLEND_ALPHA = 0;
	public static final int BLEND_ADD = 1;
	public static final int BLEND_SUBSTRACT = 2;
	public static final int BLEND_MULTIPLY = 3;
	public static final int BLEND_DODGE = 4;
	public static final int BLEND_BURN = 5;
	public static final int BLEND_SCREEN = 6;
	public static final int BLEND_OVERLAY = 7;
	public static final int BLEND_REPLACE = 8;
	public static final int BLEND_DEFAULT = 1;

	public static int parseBlend(String p_parseBlend_0_) {
		if (p_parseBlend_0_ == null) {
			return 1;
		}
		p_parseBlend_0_ = p_parseBlend_0_.toLowerCase().trim();

		if (p_parseBlend_0_.equals("alpha")) {
			return 0;
		}
		if (p_parseBlend_0_.equals("add")) {
			return 1;
		}
		if (p_parseBlend_0_.equals("subtract")) {
			return 2;
		}
		if (p_parseBlend_0_.equals("multiply")) {
			return 3;
		}
		if (p_parseBlend_0_.equals("dodge")) {
			return 4;
		}
		if (p_parseBlend_0_.equals("burn")) {
			return 5;
		}
		if (p_parseBlend_0_.equals("screen")) {
			return 6;
		}
		if (p_parseBlend_0_.equals("overlay")) {
			return 7;
		}
		if (p_parseBlend_0_.equals("replace")) {
			return 8;
		}
		Config.warn("Unknown blend: " + p_parseBlend_0_);
		return 1;
	}

	public static void setupBlend(int p_setupBlend_0_, float p_setupBlend_1_) {
		switch (p_setupBlend_0_) {
			case 0:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(770, 771);
				G.color(1.0F, 1.0F, 1.0F, p_setupBlend_1_);
				break;

			case 1:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(770, 1);
				G.color(1.0F, 1.0F, 1.0F, p_setupBlend_1_);
				break;

			case 2:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(775, 0);
				G.color(p_setupBlend_1_, p_setupBlend_1_, p_setupBlend_1_, 1.0F);
				break;

			case 3:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(774, 771);
				G.color(p_setupBlend_1_, p_setupBlend_1_, p_setupBlend_1_, p_setupBlend_1_);
				break;

			case 4:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(1, 1);
				G.color(p_setupBlend_1_, p_setupBlend_1_, p_setupBlend_1_, 1.0F);
				break;

			case 5:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(0, 769);
				G.color(p_setupBlend_1_, p_setupBlend_1_, p_setupBlend_1_, 1.0F);
				break;

			case 6:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(1, 769);
				G.color(p_setupBlend_1_, p_setupBlend_1_, p_setupBlend_1_, 1.0F);
				break;

			case 7:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(774, 768);
				G.color(p_setupBlend_1_, p_setupBlend_1_, p_setupBlend_1_, 1.0F);
				break;

			case 8:
				G.enableAlpha();
				G.disableBlend();
				G.color(1.0F, 1.0F, 1.0F, p_setupBlend_1_);
		}

		G.enableTexture2D();
	}

	public static void clearBlend(float p_clearBlend_0_) {
		G.disableAlpha();
		G.enableBlend();
		G.blendFunc(770, 1);
		G.color(1.0F, 1.0F, 1.0F, p_clearBlend_0_);
	}

}
