package net.minecraft.item;

import java.util.ArrayList;
import java.util.List;

public class Groups {

	public static final Group ROCKS = new Group("Камень", 9, 1, "1", "1:1", "1:2", "1:3", "1:4", "1:5", "1:6", "49", "121");
	public static final Group SOIL = new Group("Почвы", 9, 1, "3", "3:1", "2", "3:2", "110", "12", "12:1", "13", "82");

	public static final Group SANDSTONE = new Group("Песчаник", 6, 2, "12", "24", "128", "44:1", "24:1", "24:2",
			"12:1", "179", "180", "182", "179:1", "179:2");

	public static final Group OAK = new Group("Дуб", 9, 1, "17:0", "5:0", "53", "126:0", "85", "324", "107", "18:0", "6:0");
	public static final Group SPR = new Group("Ель", 9, 1, "17:1", "5:1", "134", "126:1", "188", "427", "183", "18:1", "6:1");
	public static final Group BIR = new Group("Берёза", 9, 1, "17:2", "5:2", "135", "126:2", "189", "428", "184", "18:2", "6:2");
	public static final Group JUN = new Group("Тропика", 9, 1, "17:3", "5:3", "136", "126:3", "190", "429", "185", "18:3", "6:3");
	public static final Group ACA = new Group("Акация", 9, 1, "162:1", "5:4", "163", "126:4", "192", "430", "187", "161", "6:4");
	public static final Group DAR = new Group("Тёмный дуб", 9, 1, "162:1", "5:5", "164", "126:5", "191", "431", "186", "161:1", "6:5");

	public static final Group STONEBRICK = new Group("Каменные кирпичи", 7, 1, "44", "98", "109", "44:5", "98:1", "98:2", "98:3");
	public static final Group COBBLESTONE = new Group("Булыжник", 7, 1, "318", "4", "67", "44:3", "139", "48", "139:1");
	public static final Group BRICK = new Group("Кирпичи", 5, 2, "336", "45", "108", "44:4", "", "405", "112", "114", "44:6", "113");
	public static final Group QUARTZ = new Group("Кварц", 6, 1, "406", "155", "156", "44:7", "155:1", "155:2");


	public static final Group RESOURCES = new Group("Ресурсы", 8, 3, "263", "265", "351:4", "331", "266", "264", "388", "406",
			"16", "15", "21", "73", "14", "56", "129", "153",
			"173", "42", "22", "152", "41", "57", "133", "155");

	public static final Group WOOL = new Group("Шерсть", 16, 1, Group.every(35, 16));
	public static final Group CLAY = new Group("Глина", 16, 1, Group.every(159, 16));
	public static final Group GLASS = new Group("Стекло", 16, 1, Group.every(95, 16));

	public static final List<Coord> uno = new ArrayList<>();
	public static final List<Coord> duo = new ArrayList<>();

	public static final int unoW;
	public static final int duoW;

	public static final int height;
	static {
		int y = 0;
		uno(ROCKS, y);
		uno(SOIL, y += 18);
		uno(SANDSTONE, y += 22);
		uno(OAK, y += 36);
		uno(SPR, y += 18);
		uno(BIR, y += 18);
		uno(JUN, y += 18);
		uno(ACA, y += 18);
		uno(DAR, y += 18);
		uno(STONEBRICK, y += 18);
		uno(COBBLESTONE, y += 18);
		uno(BRICK, y += 18);
		uno(QUARTZ, y += 18);

		height = y += 18;

		duo(RESOURCES, y = 0);
		duo(WOOL, y += 64);
		duo(CLAY, y += 18);
		duo(GLASS, y += 18);


		int i = 0;
		for (Coord coord : uno) if (coord.group.getWidth() > i) i = coord.group.getWidth();
		unoW = i;
		for (Coord coord : duo) if (coord.group.getWidth() > i) i = coord.group.getWidth();
		duoW = i;
	}

	private static Coord uno(Group g, int y) {
		Coord c = new Coord(g, y);
		uno.add(c);
		return c;
	}

	private static Coord duo(Group g, int y) {
		Coord c = new Coord(g, y);
		duo.add(c);
		return c;
	}

	public static class Coord {

		private final Group group;
		private final int y;

		public Coord(Group group, int y) {
			this.group = group;
			this.y = y;
		}

		public Group getGroup() {
			return group;
		}

		public int getY() {
			return y;
		}

	}


}
