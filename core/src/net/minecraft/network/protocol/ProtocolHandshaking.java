package net.minecraft.network.protocol;

import net.minecraft.network.handshake.client.C00Handshake;

public class ProtocolHandshaking extends Protocol {

	public ProtocolHandshaking() {
		super(-1);
		registerPacket(true, C00Handshake.class, C00Handshake::new);
	}

}
