package net.minecraft.logging;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LogReader implements ILogInterceptor {

	private final File file;
	private final List<Line> lines = new ArrayList<>();

	public LogReader(File file) {
		this.file = file;
		read();
	}

	public LogReader(Log log) {
		file = log.file;
		read();
		log.addAccessor(this);
	}

	public static Line constructLine(String s) {
		if (s.startsWith("--")) return new Line(LogLevel.COMMENT, s.substring(3), null);
		if (s.startsWith("* ")) return new Line(LogLevel.ERROR, s.substring(3), null);
		if (s.length() < 11) return new Line(LogLevel.COMMENT, s, null);
		String time = s.substring(0, 8);
		LogLevel level = LogLevel.getLogLevel(s.charAt(9));
		return new Line(level, s.substring(11), time);
	}

	private void read() {
		try {
			FileInputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
			BufferedReader r = new BufferedReader(isr);
			String s;
			while ((s = r.readLine()) != null)
				lines.add(constructLine(s));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public File getFile() {
		return file;
	}

	public List<Line> getLines() {
		return lines;
	}

	@Override
	public void intercept(LogLevel level, Date date, String message) {
		lines.add(new Line(level, message, Log.TIME.format(date)));
	}

	public static class Line {

		private final LogLevel level;
		private final char[] message, time;

		public Line(LogLevel level, String message, String time) {
			this.level = level;
			this.message = message.toCharArray();
			this.time = time == null ? new char[0] : time.toCharArray();
		}

		@Override
		public String toString() {
			return new String(time) + " " + level.getPrefix() + " " + new String(message);
		}

		public char[] getMessage() {
			return message;
		}

		public char[] getTime() {
			return time;
		}

		public LogLevel getLevel() {
			return level;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Line)) return false;
			Line l = (Line) o;
			return Arrays.equals(l.getTime(), getTime()) && Arrays.equals(l.getMessage(), getMessage()) && l.level == level;
		}

	}

}
