package net.minecraft.client.gui.font;

import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.GlStateManager;

public interface IFontRenderer {

	/**
	 * Метод для получения высоты шрифта
	 * @return Высота строки, определённая для данного шрифта
	 */
	int getFontHeight();

	/**
	 * Кол-во пикселей, на которое будет смещена тень текста.
	 */
	int getShadowOffset();

	/**
	 * Метод для рисования текста на экране
	 * @param s Текст, который требуется отрендерить
	 * @param x Отступ вправо от левого края экрана
	 * @param y Отступ вниз от верхнего края экрана
	 * @return Кол-во пикселей, на которое сместилась каретка GL, равное ширине строки
	 */
	default int renderString(String s, float x, float y, boolean shadow) {
		if (shadow) renderString0(s, x + getShadowOffset(), y + getShadowOffset(), true);
		return (int) renderString0(s, x, y, false);
	}

	default float renderString0(String s, float x, float y, boolean dark) {
		GlStateManager.pushMatrix();
		G.translate(x, y, 0);
		boolean coloring = false;
		float strikeStart = -1, underStart = -1;
		boolean bold = false, italic = false;
		float offset = 0;
		int color;
		char[] charArray = s.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (c == '§' && !coloring) {
				coloring = true;
				continue;
			}
			if (coloring) {
				coloring = false;
				int colorCode = "0123456789abcdef".indexOf(c);
				if (colorCode < 0) {
					switch (c) {
						case 'l': bold = true; break;
						case 'o': italic = true; break;
						case 'r':
							bold = italic = false;
							G.color(1, 1, 1);
							break;
					}
				} else {
					bold = italic = false;
					color = FontUtils.colorCodes[dark ? colorCode + 16 : colorCode];
					G.colorNoAlpha(color);
				}
				continue;
			}

			float translate = renderChar(c, bold, italic);

			offset += translate;
			G.translate(translate, 0, 0);

		}
		GlStateManager.popMatrix();
		return offset;
	}



	float renderChar(char c, boolean bold, boolean italic);


}
