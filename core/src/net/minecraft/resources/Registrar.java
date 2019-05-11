package net.minecraft.resources;

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
		E.getEventLib().LIB.register(domain, c, listener);
	}
	public <T extends Packet> void regInterceptor(Class<T> c, Handler<Packet, T> listener) {
		E.getPacketLib().LIB.register(domain, c, listener);
	}

	public void unregister() {
		E.getEventLib().LIB.disable(domain);
		E.getPacketLib().LIB.disable(domain);
	}

}
