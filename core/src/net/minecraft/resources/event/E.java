package net.minecraft.resources.event;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class E {

	static final PacketBridge PACKETS = new PacketBridge();

	public static <L extends INetHandler, T extends Packet<L>> boolean notify(T packet, L handler) {
		return PACKETS.call(packet, handler);
	}

	public static Bridge<Packet> getPacketLib() {
		return PACKETS;
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
