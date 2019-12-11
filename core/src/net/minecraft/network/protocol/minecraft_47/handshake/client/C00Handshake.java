package net.minecraft.network.protocol.minecraft_47.handshake.client;

import java.io.IOException;

import lombok.Getter;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.implario.ProtocolImplario;
import net.minecraft.network.protocol.minecraft_47.handshake.INetHandlerHandshakeServer;

@Getter
public class C00Handshake implements Packet<INetHandlerHandshakeServer> {
	private int protocolVersion;
	private String ip;
	private int port;
	private boolean requestedStatus;

	public C00Handshake() {}

	public C00Handshake(int version, String ip, int port, boolean requestedStatus) {
		this.protocolVersion = version;
		this.ip = ip;
		this.port = port;
		this.requestedStatus = requestedStatus;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.protocolVersion = buf.readVarIntFromBuffer();
		this.ip = buf.readStringFromBuffer(255);
		this.port = buf.readUnsignedShort();
		int id = buf.readVarIntFromBuffer();
		this.requestedStatus = id == 1;
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.protocolVersion);
		buf.writeString("Implario");
		buf.writeShort(6666);
		buf.writeVarIntToBuffer(requestedStatus ? 1 : 2);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerHandshakeServer handler) {
		handler.processHandshake(this);
	}

	@Override
	public void endSend(NetworkManager manager) {
		manager.setConnectionState(requestedStatus ? ProtocolImplario.protocol.getProtocolStatus() : ProtocolImplario.protocol.getProtocolLogin());
	}
}
