package net.minecraft.util;

public class Normalizer {
	
	public static float exp(float f, float a) {
		float x = (float) Math.pow(f, a);
		float x1 = (float) Math.pow(1 - f, a);
		return x / (x + x1);
	}
	
}
