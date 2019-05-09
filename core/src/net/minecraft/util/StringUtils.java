package net.minecraft.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class StringUtils {

	private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	private static final String[] name1 = {"Mr", "Lord", "Sir", "Dr"};
	private static final String[] name2 = {"Happy", "Merry", "Tipsy", "Strange", "Edenic", "Exotic", "Forest", "Wintry", "Leporine", "Weird", "Omega"};
	private static final String[] name3 = {"Bucket", "Waffle", "Wife", "Laptop", "Flower", "Miner", "Ham", "Pig", "Cow", "Day", "Night", "Worm"};

	final static TreeMap<Integer, String> romanian = new TreeMap<>();
	static {

		StringUtils.romanian.put(1000, "M");
		StringUtils.romanian.put(900, "CM");
		StringUtils.romanian.put(500, "D");
		StringUtils.romanian.put(400, "CD");
		StringUtils.romanian.put(100, "C");
		StringUtils.romanian.put(90, "XC");
		StringUtils.romanian.put(50, "L");
		StringUtils.romanian.put(40, "XL");
		StringUtils.romanian.put(10, "X");
		StringUtils.romanian.put(9, "IX");
		StringUtils.romanian.put(5, "V");
		StringUtils.romanian.put(4, "IV");
		StringUtils.romanian.put(1, "I");

	}

	/**
	 * Returns the time elapsed for the given number of ticks, in "mm:ss" format.
	 */
	public static String ticksToElapsedTime(int ticks) {
		int i = ticks / 20;
		int j = i / 60;
		i = i % 60;
		return i < 10 ? j + ":0" + i : j + ":" + i;
	}

	public static String stripControlCodes(String p_76338_0_) {
		return patternControlCode.matcher(p_76338_0_).replaceAll("");
	}

	/**
	 * Returns a value indicating whether the given string is null or empty.
	 */
	public static boolean isNullOrEmpty(String string) {
		return org.apache.commons.lang3.StringUtils.isEmpty(string);
	}

	public static String romanianNotation(int number) {
		if (number < 0) return "-" + romanianNotation(-number);
		if (number == 0) return "O";
		int l = romanian.floorKey(number);
		if (number == l) return romanian.get(number);
		return romanian.get(l) + romanianNotation(number - l);
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 3];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 3] = hexArray[v >>> 4];
			hexChars[j * 3 + 1] = hexArray[v & 0x0F];
			hexChars[j * 3 + 2] = ' ';
		}
		return new String(hexChars);
	}

	public static String getWittyName() {
		return  name1[(int) (Math.random() * name1.length)] +
				name2[(int) (Math.random() * name2.length)] +
				name3[(int) (Math.random() * name3.length)];
	}

	public static String imageToBase64(String path) throws IOException {

		byte[] bytes = IOUtils.toByteArray(new FileInputStream(path));
		return new String(Base64.encodeBase64(bytes), UTF_8);

	}

	public static String imageToBase64(BufferedImage img) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "PNG", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return new String(Base64.encodeBase64(imageInByte), UTF_8);

	}

	public static String extractLetters(String text) {
		StringBuilder b = new StringBuilder();
		boolean colorCode = false;
		for (char c : text.toCharArray()) {
			if (colorCode) {
				colorCode = false;
				continue;
			}
			// Замена полноширинных символов на их обыкновенные аналоги.
			if (c >= 0xFF10 && c < 0xFF5B) c = (char) ((int) c - 0xFEE0);
			if (c == '§') {
				colorCode = true;
				continue;
			}
			if (c == '.' || c == '\n') c = ' ';
			if (
					c >= 'A' && c <= 'Z' ||
					c >= 'a' && c <= 'z' ||
					c >= 'А' && c <= 'Я' ||
					c >= 'а' && c <= 'я' ||
					c == ' ' && b.length() != 0
				) b.append(c);
		}
		return b.toString();
	}

}
