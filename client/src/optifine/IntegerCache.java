package optifine;

public class IntegerCache {
	private static final int CACHE_SIZE = 4096;
	private static final int[] cache = makeCache();

	private static int[] makeCache() {
		int[] ainteger = new int[CACHE_SIZE];

		for (int i = 0; i < CACHE_SIZE; ++i)
			ainteger[i] = i;

		return ainteger;
	}

	public static int valueOf(int i) {
		return i >= 0 && i < CACHE_SIZE ? cache[i] : i;
	}
}
