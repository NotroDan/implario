package net.minecraft.util.chat;

public class ChatAllowedCharacters {

	/**
	 * Array of the special characters that are not allowed in any text drawing of Minecraft.
	 */
	public static final char[] disallowedChars = new char[] {'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

	public static boolean isAllowedCharacter(char character) {
		return character != 167 && character >= 32 && character != 127;
	}

	/**
	 * Filter string by only keeping those characters for which isAllowedCharacter() returns true.
	 */
	public static String filterAllowedCharacters(String input) {
		StringBuilder stringbuilder = new StringBuilder();

		for (char c0 : input.toCharArray()) {
			if (isAllowedCharacter(c0)) {
				stringbuilder.append(c0);
			}
		}

		return stringbuilder.toString();
	}

}
