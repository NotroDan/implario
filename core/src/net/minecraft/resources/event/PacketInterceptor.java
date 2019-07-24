package net.minecraft.resources.event;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public interface PacketInterceptor<H extends INetHandler, T extends Packet<H>> extends Handler<Packet, T> {

	@Override
	default void handle(T data) {}

	/**
	 * Метод, вызывающийся при получении пакета заданного типа.
	 *
	 * @param data    Перехваченный пакет
	 * @param handler Слушатель, поймавший этот пакет
	 * @return Должен ли слушатель обрабатывать его
	 */
	boolean handle(T data, H handler);

}
