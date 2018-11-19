package net.minecraft.util;

import java.util.function.Function;

public class ASCIITable {

	private static final String s = "" +
			"┏━━━━━━━━━━━━━━━━━┳━━━━━━┳━━━━━━━┳━━━━━━━━┳━━━━━━┳━━━━━━━━┓\n" +
			"┃ name            ┃ elo  ┃ kills ┃ deaths ┃ wins ┃ played ┃\n" +
			"┡━━━━━━━━━━━━━━━━━╇━━━━━━╇━━━━━━━╇━━━━━━━━╇━━━━━━╇━━━━━━━━┩\n" +
			"│ HoroshuParen    │ 9534 │   140 │     65 │   48 │    119 │\n" +
			"│ OnlySegmanPvp   │ 1627 │    49 │     19 │   21 │     41 │\n" +
			"│ oRussianBlatant │ 1340 │    21 │     19 │    5 │     25 │\n" +
			"│ Destrex         │ 1271 │    44 │     11 │   16 │     27 │\n" +
			"│ KOTICK          │ 1147 │    50 │     11 │   13 │     25 │\n" +
			"│ zSa1qer         │  708 │    32 │     18 │    6 │     26 │\n" +
			"│ sqdCody         │  675 │    26 │     22 │    6 │     29 │\n" +
			"│ Jeusex          │  519 │    20 │     16 │    5 │     21 │\n" +
			"│ YaClary         │  500 │    24 │     30 │    2 │     33 │\n" +
			"│ _KiLLeR_DaN_    │  450 │    20 │     12 │    4 │     16 │\n" +
			"│ aloxakiks       │  444 │     9 │      1 │    7 │      8 │\n" +
			"│ CAMbIY          │  434 │    17 │      6 │    5 │     11 │\n" +
			"│ xErkaSolar      │  363 │    15 │     12 │    3 │     15 │\n" +
			"│ Golubika        │  361 │     6 │     10 │    1 │     11 │\n" +
			"│ KrampusCekcu    │  357 │    10 │      3 │    5 │      8 │\n" +
			"│ Honey_          │  339 │    12 │     15 │    3 │     21 │\n" +
			"│ UHC_one_love    │  337 │    13 │     35 │    1 │     36 │\n" +
			"│ _Mazull_        │  293 │     4 │     10 │    1 │     12 │\n" +
			"│ Ermoha01        │  285 │     8 │      2 │    4 │      6 │\n" +
			"│ FrozenNaN_PVP   │  253 │    13 │      6 │    2 │      8 │\n" +
			"│ GhastLPvP       │  244 │    10 │      8 │    2 │     10 │\n" +
			"│ Agera00a        │  226 │    12 │     13 │    1 │     14 │\n" +
			"│ _ZeninPlay_     │  210 │     4 │     11 │    2 │     13 │\n" +
			"│ Kvell4ik        │  207 │     9 │     12 │    1 │     14 │\n" +
			"│ TTrostochek     │  192 │     7 │      3 │    2 │      5 │\n" +
			"│ VikaPro_228     │  191 │     4 │      9 │    2 │     10 │\n" +
			"│ Implementation  │  188 │     9 │      8 │    1 │     10 │\n" +
			"│ flagdog         │  186 │     9 │     11 │    1 │     13 │\n" +
			"│ Centenario      │  178 │     7 │     11 │    1 │     12 │\n" +
			"│ I_love_UHC      │  153 │     3 │     14 │    1 │     15 │\n" +
			"└─────────────────┴──────┴───────┴────────┴──────┴────────┘";


	@SafeVarargs
	public static <T> String format(String[] columns, T[] objects, Function<T, ?>... extractors) {
		StringBuilder b = new StringBuilder("┏");
		String[][] v = new String[columns.length][objects.length];
		int[] w = new int[columns.length];
		StringBuilder separator = new StringBuilder("┡");
		StringBuilder footer = new StringBuilder("└");
		for (int i = 0; i < v.length; i++) {
			String[] ss = v[i];
			for (int i1 = 0; i1 < objects.length; i1++) ss[i1] = String.valueOf(extractors[i].apply(objects[i1]));
			String title = columns[i];
			int width = title.length();
			for (String s : ss)
				if (s.length() > width) width = s.length();
			for (int j = 0; j < width + 2; j++) {
				b.append("━");
				separator.append("━");
				footer.append("─");
			}
			w[i] = width;
			b.append(i + 1 == v.length ? "┓" : "┳");
			separator.append(i + 1 == v.length ? "┩" : "╇");
			footer.append(i + 1 == v.length ? "┘" : "┴");
		}
		b.append('\n');
		for (int i = 0; i < v.length; i++) {
			String s = columns[i];
			b.append("┃ ").append(s);
			int width = w[i] - s.length() + 1;
			for (int j = 0; j < width; j++) b.append(' ');
		}
		b.append("┃\n").append(separator.toString()).append('\n');

		for (T o : objects) {
			b.append("│ ");
			for (int i = 0; i < columns.length; i++) {
				Object e = extractors[i].apply(o);
				String s = String.valueOf(e);
				int width = w[i] - s.length();
				StringBuilder spaces = new StringBuilder();
				for (int j = 0; j < width; j++) spaces.append(' ');
				if (e instanceof Number) s = spaces.toString() + s;
				else s += spaces.toString();
				b.append(s).append(" │ ");
			}
			b.append('\n');
		}
		b.append(footer);

		return b.toString();
	}

}
