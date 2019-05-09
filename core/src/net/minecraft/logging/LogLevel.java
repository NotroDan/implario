package net.minecraft.logging;

public enum LogLevel {
	INFO(">", 0xffffff),
	IMPORTANT("=", 0xffff8a),
	WARNING("!", 0xffae29),
	ERROR("*", 0xff3f29),
	COMMENT("--", 0x909090),
	DEBUG("?", 0x298aff);

	private final String prefix;
	private final int color;

	LogLevel(String prefix, int color) {
		this.prefix = prefix;
		this.color = color;
	}

	public static LogLevel getLogLevel(char prefix) {
		switch (prefix) {
			case '>': return INFO;
			case '=': return IMPORTANT;
			case '!': return WARNING;
			case '*': return ERROR;
			default: return COMMENT;
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public int getColor() {
		return color;
	}
}
