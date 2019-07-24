package net.minecraft.client.gui.ingame.hotbar;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Scale {

	// Координаты левого нижнего угла шкалы (или правого нижнего при развороте)
	protected final int x, y;

	// Название шкалы
	protected final String name;

	// Развёрнута ли шкала в положение, при котором значение возрастает справа налево
	protected final boolean reverse;

	/* Кол-во делений, которые заполняются по вертикали прежде, чем происходит сдвиг по горизонтали

	fill == 1
		__________
		#####_____

	fill == 2
		##________
		###_______
	*/
	protected final int fill;

	/**
	 * Рендерит шкалу на экране, используя заданные параметры
	 *
	 * @param value      Текущее значение шкалы
	 * @param max        Максимальное значение шкалы
	 * @param additional Дополнительное значение шкалы, сверх максимального (Полезно при баффах, например Absorption)
	 */
	protected abstract void render(int value, int max, int additional);

}
