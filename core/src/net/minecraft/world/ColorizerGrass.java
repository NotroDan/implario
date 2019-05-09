package net.minecraft.world;

public class ColorizerGrass {

	/**
	 * Color buffer for grass
	 */
	private static int[] grassBuffer = new int[65536];

	public static void setGrassBiomeColorizer(int[] p_77479_0_) {
		grassBuffer = p_77479_0_;
	}

	public static int getGrassColor(double temperature, double humidity) {
		humidity *= temperature;
		int i = (int) ((1.0D - temperature) * 255.0D);
		int j = (int) ((1.0D - humidity) * 255.0D);
		int k = j << 8 | i;
		return k > grassBuffer.length ? 0xffff00ff : grassBuffer[k];
	}

}
