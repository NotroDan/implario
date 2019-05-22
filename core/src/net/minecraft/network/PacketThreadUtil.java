package net.minecraft.network;

import net.minecraft.resources.event.E;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {

	public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packet, final T handler, IThreadListener listener) throws ThreadQuickExitException {
		if (listener.isCallingFromMinecraftThread()) {
			boolean notify = E.notify(packet, handler);
			if (notify) throw ThreadQuickExitException.EXIT_EXCEPTION;
			return;
		}
		listener.addScheduledTask(() -> packet.processPacket(handler));
		throw ThreadQuickExitException.EXIT_EXCEPTION;
	}

}
