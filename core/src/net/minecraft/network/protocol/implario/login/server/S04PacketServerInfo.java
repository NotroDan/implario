package net.minecraft.network.protocol.implario.login.server;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.implario.login.INetHandlerLoginClientImplario;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginServer;

import java.io.IOException;

public class S04PacketServerInfo implements Packet<INetHandlerLoginClientImplario> {
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {}

	@Override
	public void processPacket(INetHandlerLoginClientImplario handler) {
		handler.processServerInfo(this);
	}
}
