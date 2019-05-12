package net.minecraft.tileentity;

import com.google.common.collect.Maps;
import net.minecraft.item.EnumDyeColor;

import java.util.Map;
import java.util.Random;

public class BeaconColors {

	public static final Map<EnumDyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(EnumDyeColor.class);

	public static float[] getColor(EnumDyeColor dyeColor) {
		return (float[]) DYE_TO_RGB.get(dyeColor);
	}

	/**
	 * Chooses a "vanilla" sheep color based on the provided random.
	 */
	public static EnumDyeColor getRandomSheepColor(Random random) {
		int i = random.nextInt(100);
		return i < 5 ? EnumDyeColor.BLACK : i < 10 ? EnumDyeColor.GRAY : i < 15 ? EnumDyeColor.SILVER : i < 18 ? EnumDyeColor.BROWN : random.nextInt(500) == 0 ? EnumDyeColor.PINK : EnumDyeColor.WHITE;
	}


	static {
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.WHITE, new float[] {1.0F, 1.0F, 1.0F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.ORANGE, new float[] {0.85F, 0.5F, 0.2F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.MAGENTA, new float[] {0.7F, 0.3F, 0.85F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.LIGHT_BLUE, new float[] {0.4F, 0.6F, 0.85F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.YELLOW, new float[] {0.9F, 0.9F, 0.2F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.LIME, new float[] {0.5F, 0.8F, 0.1F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.PINK, new float[] {0.95F, 0.5F, 0.65F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.GRAY, new float[] {0.3F, 0.3F, 0.3F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.SILVER, new float[] {0.6F, 0.6F, 0.6F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.CYAN, new float[] {0.3F, 0.5F, 0.6F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.PURPLE, new float[] {0.5F, 0.25F, 0.7F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.BLUE, new float[] {0.2F, 0.3F, 0.7F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.BROWN, new float[] {0.4F, 0.3F, 0.2F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.GREEN, new float[] {0.4F, 0.5F, 0.2F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.RED, new float[] {0.6F, 0.2F, 0.2F});
		BeaconColors.DYE_TO_RGB.put(EnumDyeColor.BLACK, new float[] {0.1F, 0.1F, 0.1F});
	}

}
