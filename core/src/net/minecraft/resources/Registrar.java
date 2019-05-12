package net.minecraft.resources;

import net.minecraft.item.Item;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.resources.event.*;

public class Registrar {

	private final Domain domain;

	public Registrar(Domain domain) {
		this.domain = domain;
	}

	public Domain getDomain() {
		return domain;
	}

	public <T extends Event> void regListener(Class<T> c, Handler<Event, T> listener) {
		E.getEventLib().registerListener(domain, c, listener);
	}
	public <L extends INetHandler, T extends Packet<L>> void regInterceptor(Class<T> c, PacketInterceptor<L, T> listener) {
		E.getPacketLib().registerListener(domain, c, listener);
	}

	public void unregister() {
		E.getEventLib().LIB.disable(domain);
		E.getPacketLib().LIB.disable(domain);
	}

	public void registerItem(int id, String textual, Item item) {
		// ToDo: intercept
		Item.registerItem(id, textual, item);
	}

}
