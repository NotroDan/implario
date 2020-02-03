package net.minecraft.network;

import net.minecraft.resources.event.E;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MinecraftCore;

import java.util.concurrent.Executors;

public class PacketThreadUtil {

	public static <T extends INetHandler> void syncPacket(final Packet<T> packet, final T handler, MinecraftCore core) throws ThreadQuickExitException {
		if (core.isCallingFromMinecraftThread()) {
			boolean notify = E.notify(packet, handler);
			if (notify) throw ThreadQuickExitException.EXIT_EXCEPTION;
			return;
		}
		core.queue(Executors.callable(() -> packet.processPacket(handler)));
		throw ThreadQuickExitException.EXIT_EXCEPTION;
	}

}
