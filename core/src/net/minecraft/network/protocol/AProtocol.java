package net.minecraft.network.protocol;

import lombok.Getter;
import net.minecraft.logging.Log;
import net.minecraft.network.Packet;
import net.minecraft.util.IntDoubleMap;

import java.util.function.Supplier;

public class AProtocol implements IProtocol{

	@Getter
	private final int id;
	private final IntDoubleMap<Class<? extends Packet>, Supplier<? extends Packet>>
			clientPackets = new IntDoubleMap<>(10),
			serverPackets = new IntDoubleMap<>(10);
	private int idClientPacket, idServerPacket;

	public AProtocol(int protocolId) {
		this.id = protocolId;
	}

	public <T extends Packet> void registerPacket(boolean isClientPacket, Class<T> packetClass, Supplier<T> supplier) {
		IntDoubleMap<Class<? extends Packet>, Supplier<? extends Packet>> map = isClientPacket ? clientPackets : serverPackets;

		if (map.get(packetClass) != -1) {
			int existingId = map.get(packetClass);
			String msg = (isClientPacket ? "Client-side" : "Server-side") + " packet " + packetClass + " is already known to ID " + existingId;
			Log.MAIN.error(msg);
			throw new IllegalArgumentException(msg);
		}

		map.put(isClientPacket ? idClientPacket++ : idServerPacket++, packetClass, supplier);
	}

	@Override
	public int getPacketID(boolean isClientPacket, Packet packet) {
		return (isClientPacket ? clientPackets : serverPackets).get(packet.getClass());
	}

	@Override
	public Packet getPacket(boolean isClientPacket, int packetId) {
		Supplier<? extends Packet> supplier = (isClientPacket ? clientPackets : serverPackets).get(packetId);
		return supplier == null ? null : supplier.get();
	}
}
