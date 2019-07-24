package net.minecraft.client.gui.keymap;

public enum Key {
	ESCAPE(1, "esc", "Escape"),

	DIGIT_1(2, "1"),
	DIGIT_2(3, "2"),
	DIGIT_3(4, "3"),
	DIGIT_4(5, "4"),
	DIGIT_5(6, "5"),
	DIGIT_6(7, "6"),
	DIGIT_7(8, "7"),
	DIGIT_8(9, "8"),
	DIGIT_9(10, "9"),
	DIGIT_0(11, "0"),

	MINUS(12, "-", "Минус"),
	EQUALS(13, "=", "Равно"),
	BACKSPACE(14, "\u232b", "Backspace"),
	TAB(15, "\u21c6", "Tab"),

	Q(16),
	W(17),
	E(18),
	R(19),
	T(20),
	Y(21),
	U(22),
	I(23),
	O(24),
	P(25),
	A(30),
	S(31),
	D(32),
	F(33),
	G(34),
	H(35),
	J(36),
	K(37),
	L(38),
	Z(44),
	X(45),
	C(46),
	V(47),
	B(48),
	N(49),
	M(50),

	ENTER(28, "\u21b5", "Энтер"),
	LCONTROL(29, "Ctrl", "Левый Ctrl"),
	RCONTROL(157, "Ctrl", "Правый Ctrl"),
	LALT(56, "Alt", "Левый Alt"),
	RALT(184, "Alt", "Правый Alt"),
	LSHIFT(42, "⇧", "Левый Shift"),
	RSHIFT(54, "⇧", "Правый Shift"),
	LWIN(219, "\uF8FF", "Левый Win"),
	SPACE(57, " ", "Пробел"),
	CAPS_LOCK(58, "\u21ea", "Caps Lock"),
	NUM_LOCK(69, "Num", "Num Lock", "num"),
	SCROLL_LOCK(70, "Scroll", "Scroll Lock", "sl"),
	MENU(184, "\u2263", "Menu"),
	PRINTSCREEN(183, "ps", "PrintScreen"),

	LEFT_BRACKET(26, "[", "Левая квадратная скобка"),
	RIGHT_BRACKET(27, "]", "Правая квадратная скобка"),
	SEMILOCON(39, ";", "Точка с запятой"),
	APOSTROPHE(40, "'", "Апостроф"),
	TILDA(41, "~", "Тильда"),
	BACKSLASH(43, "\\", "Обратный слэш"),
	COMMA(51, ",", "Запятая"),
	DOT(52, ".", "Точка"),
	SLASH(53, "/", "Слэш"),

	F1(59),
	F2(60),
	F3(61),
	F4(62),
	F5(63),
	F6(64),
	F7(65),
	F8(66),
	F9(67),
	F10(68),
	F11(87),
	F12(88),

	NUMPAD_MULTIPLY(55, "*", "NumPad-Умножение"),


	INSERT(210, "ins", "Insert"),
	HOME(199, "home", "Home", "hm"),
	PAGE_UP(201, "pgup", "Page Up", "pgu"),
	PAGE_DOWN(209, "pgdn", "Page Down", "pgd"),
	DELETE(211, "del", "Delete"),
	END(207, "end", "End"),
	PAUSE(197, "pause", "Pause", "pb"),

	ARROW_UP(200, "\u2191", "Вверх"),
	ARROW_RIGHT(205, "\u2192", "Вправо"),
	ARROW_LEFT(203, "\u2190", "Влево"),
	ARROW_DOWN(208, "\u2193", "Вниз"),


	;

	private final int key;
	private final String caption, name;
	private String shortAlternative;

	Key(int key) {
		this.key = key;
		this.caption = toString();
		this.name = toString();
	}

	Key(int key, String caption, String name, String shortAlternative) {
		this.key = key;
		this.caption = caption;
		this.name = name;
		this.shortAlternative = shortAlternative;
	}

	Key(int key, String caption) {
		this(key, caption, caption);
	}

	Key(int key, String caption, String name) {
		this.key = key;
		this.caption = caption;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getKey() {
		return key;
	}

	public String getCaption() {
		return caption;
	}

	public String getShortAlternative() {
		return shortAlternative == null ? caption : shortAlternative;
	}
}
