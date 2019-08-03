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
	public static final int BLEND_DEFAULT = BLEND_ADD;

	public static int parseBlend(String blend) {
		if (blend == null)
			return BLEND_DEFAULT;

		blend = blend.toLowerCase().trim();

		if (blend.equals("alpha"))
			return BLEND_ALPHA;
		if (blend.equals("add"))
			return BLEND_ADD;
		if (blend.equals("subtract"))
			return BLEND_SUBSTRACT;
		if (blend.equals("multiply"))
			return BLEND_MULTIPLY;
		if (blend.equals("dodge"))
			return BLEND_DODGE;
		if (blend.equals("burn"))
			return BLEND_BURN;
		if (blend.equals("screen"))
			return BLEND_SCREEN;
		if (blend.equals("overlay"))
			return BLEND_OVERLAY;
		if (blend.equals("replace"))
			return BLEND_REPLACE;
		Config.warn("Unknown blend: " + blend);
		return 1;
	}

	public static void setupBlend(int blendType, float blendSupport) {
		switch (blendType) {
			case 0:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(770, 771);
				G.color(1.0F, 1.0F, 1.0F, blendSupport);
				break;

			case 1:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(770, 1);
				G.color(1.0F, 1.0F, 1.0F, blendSupport);
				break;

			case 2:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(775, 0);
				G.color(blendSupport, blendSupport, blendSupport, 1.0F);
				break;

			case 3:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(774, 771);
				G.color(blendSupport, blendSupport, blendSupport, blendSupport);
				break;

			case 4:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(1, 1);
				G.color(blendSupport, blendSupport, blendSupport, 1.0F);
				break;

			case 5:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(0, 769);
				G.color(blendSupport, blendSupport, blendSupport, 1.0F);
				break;

			case 6:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(1, 769);
				G.color(blendSupport, blendSupport, blendSupport, 1.0F);
				break;

			case 7:
				G.disableAlpha();
				G.enableBlend();
				G.blendFunc(774, 768);
				G.color(blendSupport, blendSupport, blendSupport, 1.0F);
				break;

			case 8:
				G.enableAlpha();
				G.disableBlend();
				G.color(1.0F, 1.0F, 1.0F, blendSupport);
		}

		G.enableTexture2D();
	}

	public static void clearBlend(float alpha) {
		G.disableAlpha();
		G.enableBlend();
		G.blendFunc(770, 1);
		G.color(1.0F, 1.0F, 1.0F, alpha);
	}
}
