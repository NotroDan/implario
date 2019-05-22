package net.minecraft.resources.event;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class E {

	static final EventBridge EVENTS = new EventBridge();
	static final PacketBridge PACKETS = new PacketBridge();

	public static <T extends Event> T call(T event) {
		EVENTS.call(event);
		return event;
	}

	public static <L extends INetHandler, T extends Packet<L>> boolean notify(T packet, L handler) {
		return PACKETS.call(packet, handler);
	}

	public static Bridge<Event> getEventLib() {
		return EVENTS;
	}

	public static Bridge<Packet> getPacketLib() {
		return PACKETS;
	}

	private static class EventBridge extends Bridge<Event> {

		public <T extends Event> T call(T event) {
			Class<T> type = (Class<T>) event.getClass();
			for (HandlerLibrary<Event>.Cell<T> handler : LIB.getListeners(type))
				handler.getHandler().handle(event);
			return event;
		}

	}

	private static class PacketBridge extends Bridge<Packet> {

		public <L extends INetHandler, T extends Packet<L>> boolean call(T packet, L handler) {
			Class<T> type = (Class<T>) packet.getClass();
			boolean global = false;
			for (HandlerLibrary<Packet>.Cell<T> h : LIB.getListeners(type)) {
				boolean result = ((PacketInterceptor) h.getHandler()).handle(packet, handler);
				if (result) global = true;
			}
			return global;
		}

	}

}
