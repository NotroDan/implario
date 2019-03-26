package net.minecraft.client.gui.font;

public interface IFontRenderer {

	/**
	 * Метод для рисования текста на экране
	 * @param s Текст, который требуется отрендерить
	 * @param x Отступ вправо от левого края экрана
	 * @param y Отступ вниз от верхнего края экрана
	 * @param color Цвет текста в формате 0xAARRGGBB
	 * @param shadow Рендерить ли тень текста
	 * @return Кол-во пикселей, на которое сместилась каретка GL, равное ширине строки
	 */
	int drawString(String s, float x, float y, int color, boolean shadow);

	/**
	 * Метод для получения высоты шрифта
	 * @return Высота строки, определённая для данного шрифта
	 */
	int getFontHeight();

}
