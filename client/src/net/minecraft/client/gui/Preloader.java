package net.minecraft.client.gui;

import lombok.Getter;
import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

public abstract class Preloader {

	public static final String[] states = {
			"Соединение с графическим ядром",
			"Загрузка настроек",
			"Загрузка стандартного ресурс-пака",
			"Запуск обработчика чанков",
			"Загрузка звукового ядра",
			"Проверка готовности систем",
			"Запуск движка рендеринга",
			"Загрузка моделей блоков",
			"Загрузка вариантов моделей",
			"Загрузка проверки моделей",
			"Загрузка спрайтов",
			"Запекание моделей предметов",
			"Запекание моделей блоков",
			"Рендер предметов",
			"Рендер мобов",
			"Рендер блоков",
			"Компоновка глобального рендера",
			"Можно играть!"
	};

	@Getter
	protected final Tessellator tessellator = new Tessellator(2097152);
	protected final ScaledResolution res;
	protected final Minecraft mc = MC.i();
	protected volatile int state = 3;
	private final Object[] lock = {};


	public Preloader(ScaledResolution res) {
		this.res = res;
	}

	public abstract void render();

	public abstract void header();

	public void nextState() {
		synchronized (lock) {
			this.state++;
		}
	}

	public void dissolve() {
		this.state = 0;
	}

}
