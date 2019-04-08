package net.minecraft.client.gui.font;

public class FontUtils {

	// Массив со значениями цветовых кодов от 0 до f, а также теней этих кодов
	public static int[] colorCodes = new int[32];

	static {

		for (int i = 0; i < 32; ++i) {
			int j = (i / 8 & 1) * 85;
			int k = (i / 4 & 1) * 170 + j;
			int l = (i / 2 & 1) * 170 + j;
			int i1 = (i & 1) * 170 + j;

			if (i == 6) {
				k += 85;
			}

			if (i >= 16) {
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			colorCodes[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}
	}

}
