package net.minecraft.resources;

public interface ServerSideDatapack {

	/**
	 * Преинициализация.
	 * На этом этапе нужно заменять основные понятия, генераторы.
	 * Примеры: регистрация сущностей, изменение WorldServiceProveder
	 */
	void preinit();

	/**
	 * Основная инициализация.
	 * Здесь регистрируются слушатели событий, пакетов, внутриигровых гуишек
	 */
	void init();

}
