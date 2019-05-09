package net.minecraft.util;

public class IntegerCache {

	private static final Integer[] a = new Integer[65535];

	public static Integer func_181756_a(int b) {
		return b > 0 && b < a.length ? a[b] : Integer.valueOf(b);
	}

	static {
		int i = 0;
		for (int j = a.length; i < j; ++i) a[i] = i;
	}
}
