package net.minecraft.util;

public class TextExtractor {
	
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
