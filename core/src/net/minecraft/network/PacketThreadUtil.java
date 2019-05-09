package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {

	public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packet, final T type, IThreadListener listener) throws ThreadQuickExitException {
		if (listener.isCallingFromMinecraftThread()) return;
		listener.addScheduledTask(() -> packet.processPacket(type));
		throw ThreadQuickExitException.EXIT_EXCEPTION;
	}

}
