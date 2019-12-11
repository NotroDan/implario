package net.minecraft.network.protocol.minecraft_47.handshake;

import net.minecraft.network.protocol.minecraft_47.handshake.client.C00Handshake;
import net.minecraft.network.protocol.Protocol;

public class ProtocolHandshaking47 extends Protocol {
	public ProtocolHandshaking47() {
		super(-1);
		registerPacket(true, C00Handshake.class, C00Handshake::new);
	}
}
