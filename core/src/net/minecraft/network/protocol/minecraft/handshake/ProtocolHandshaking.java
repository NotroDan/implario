package net.minecraft.network.protocol.minecraft.handshake;

import net.minecraft.network.Packet;
import net.minecraft.network.protocol.AProtocol;

public class ProtocolHandshaking extends AProtocol implements IProtocolHandshake {
	public ProtocolHandshaking() {
		super(-1);
		registerPacket(true, C00Handshake.class, C00Handshake::new);
	}

	@Override
	public Packet<INetHandlerHandshakeServer> getHandshake(int version, boolean requireStatus) {
		return new C00Handshake(version, requireStatus);
	}
}
