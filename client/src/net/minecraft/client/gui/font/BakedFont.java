package net.minecraft.client.gui.font;

public enum BakedFont {

	SEGOE_BIG("Segoe UI", 72),

	ARIAL("Arial Unicode MS", 22),
	SEGOE("Segoe UI", 22),
	VERDANA("Verdana", 22),
	CALIBRI("Calibri", 22),

	SEGOE_SMALL("Segoe UI", 15),
	VERDANA_SMALL("Verdana", 15),
	CALIBRI_SMALL("Calibri", 15),

	;


	private final String fontname;
	private final int size;
	private TrueTypeFontRenderer renderer;

	BakedFont(String fontname, int size) {
		this.fontname = fontname;
		this.size = size;
	}

	public String getFontname() {
		return fontname;
	}

	public TrueTypeFontRenderer getRenderer() {
		return renderer == null ? renderer = new TrueTypeFontRenderer(fontname, size) : renderer;
	}


}
