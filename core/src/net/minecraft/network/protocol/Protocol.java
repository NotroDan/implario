package net.minecraft.network.protocol;

import lombok.Getter;
import net.minecraft.logging.Log;
import net.minecraft.network.Packet;
import net.minecraft.network.protocol.minecraft_47.handshake.client.C00Handshake;
import net.minecraft.network.protocol.minecraft_47.login.client.C00PacketLoginStart;
import net.minecraft.network.protocol.minecraft_47.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.protocol.minecraft_47.login.server.S00PacketDisconnect;
import net.minecraft.network.protocol.minecraft_47.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.protocol.minecraft_47.login.server.S02PacketLoginSuccess;
import net.minecraft.network.protocol.minecraft_47.login.server.S03PacketEnableCompression;
import net.minecraft.network.protocol.minecraft_47.play.client.*;
import net.minecraft.network.protocol.minecraft_47.play.server.*;
import net.minecraft.network.protocol.minecraft_47.status.client.C00PacketServerQuery;
import net.minecraft.network.protocol.minecraft_47.status.client.C01PacketPing;
import net.minecraft.network.protocol.minecraft_47.status.server.S00PacketServerInfo;
import net.minecraft.network.protocol.minecraft_47.status.server.S01PacketPong;
import net.minecraft.util.IntDoubleMap;

import java.util.function.Supplier;

public class Protocol implements IProtocol{

	@Getter
	private final int id;
	private final IntDoubleMap<Class<? extends Packet>, Supplier<? extends Packet>>
			clientPackets = new IntDoubleMap<>(10),
			serverPackets = new IntDoubleMap<>(10);
	private int idClientPacket, idServerPacket;

	public Protocol(int protocolId) {
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
