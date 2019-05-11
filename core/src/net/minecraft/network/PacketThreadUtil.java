package net.minecraft.network;

import net.minecraft.resources.event.E;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {

	public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packet, final T handler, IThreadListener listener) throws ThreadQuickExitException {
		if (listener.isCallingFromMinecraftThread()) {
			E.notify(packet, handler);
			return;
		}
		listener.addScheduledTask(() -> packet.processPacket(handler));
		throw ThreadQuickExitException.EXIT_EXCEPTION;
	}

}
