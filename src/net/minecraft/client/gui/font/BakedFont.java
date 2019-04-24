package net.minecraft.client.gui.font;

public enum BakedFont {

	ARIAL("Arial Unicode MS"),
	SEGOE("Segoe UI"),
	VERDANA("Verdana");


	private final String fontname;
	private TrueTypeFontRenderer renderer;

	BakedFont(String fontname) {
		this.fontname = fontname;
	}

	public String getFontname() {
		return fontname;
	}

	public TrueTypeFontRenderer getRenderer() {
		return renderer == null ? renderer = new TrueTypeFontRenderer(fontname, 22) : renderer;
	}


}
