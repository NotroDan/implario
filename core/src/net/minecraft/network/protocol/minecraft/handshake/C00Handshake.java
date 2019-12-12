package net.minecraft.network.protocol.minecraft.handshake;

import java.io.IOException;

import lombok.Getter;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.implario.ProtocolImplario;
import net.minecraft.network.protocol.minecraft.handshake.INetHandlerHandshakeServer;

@Getter
class C00Handshake implements Packet<INetHandlerHandshakeServer> {
	private int protocolVersion;
	private String ip;
	private boolean requestedStatus;

	public C00Handshake() {}

	public C00Handshake(int version, boolean requestedStatus) {
		this.protocolVersion = version;
		this.requestedStatus = requestedStatus;
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.protocolVersion = buf.readVarIntFromBuffer();
		this.ip = buf.readStringFromBuffer(255);
		buf.readUnsignedShort();
		int id = buf.readVarIntFromBuffer();
		this.requestedStatus = id == 1;
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(this.protocolVersion);
		buf.writeString("Implario");
		buf.writeShort(6666);
		buf.writeVarIntToBuffer(requestedStatus ? 1 : 2);
	}

	@Override
	public void processPacket(INetHandlerHandshakeServer handler) {
		handler.processHandshake(this);
	}

	@Override
	public void endSend(NetworkManager manager) {
		manager.setConnectionState(requestedStatus ? ProtocolImplario.protocol.getProtocolStatus() : ProtocolImplario.protocol.getProtocolLogin());
	}
}
