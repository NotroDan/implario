package net.minecraft.resources;

import net.minecraft.entity.player.Module;
import net.minecraft.entity.player.ModuleManager;
import net.minecraft.server.Todo;

public abstract class Datapack {
	private final String domain;
	protected final Registrar registrar;
	public Object clientSide;

	public Datapack(String domain) {
		this.domain = domain;
		this.registrar = new Registrar(domain);
	}

	public String getDomain() {
		return domain;
	}

	public static boolean isServerSide() {
		return Todo.instance.isServerSide();
	}

	/**
	 * Преинициализация.
	 * На этом этапе нужно заменять основные понятия, генераторы, команды.
	 * Примеры: регистрация сущностей, изменение WorldServiceProveder
	 */
	public void preinit(){}

	/**
	 * Основная инициализация.
	 * Здесь регистрируются слушатели событий, пакетов, внутриигровых гуишек
	 */
	public void init(){}

	public void disable() {
		registrar.unregister();
	}

	protected void unload(){}

	public void loadBlocks() {}

	public void loadItems() {}

	public byte[] saveState(){
		return null;
	}

	public void loadState(byte array[]){}

	public Registrar getRegistrar() {
		return registrar;
	}

	public ModuleManager<? extends Module> moduleManager(){
		return null;
	}

	@Override
	public String toString() {
		return domain;
	}
}
