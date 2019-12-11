package net.minecraft.network.protocol.minecraft_47.login.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.minecraft_47.login.INetHandlerLoginServer;

import java.io.IOException;

public class SPacketServerInfo implements Packet<INetHandlerLoginServer> {


	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		int size = buf.readVarIntFromBuffer();
		for (int i = 0; i < size; i++) {

		}
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {

	}

	@Override
	public void processPacket(INetHandlerLoginServer handler) {

	}

}
