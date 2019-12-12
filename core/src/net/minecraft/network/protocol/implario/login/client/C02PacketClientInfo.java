package net.minecraft.network.protocol.implario.login.client;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.implario.login.INetHandlerLoginServerImplario;

import java.io.IOException;
import java.util.EnumMap;

public class C02PacketClientInfo implements Packet<INetHandlerLoginServerImplario> {
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {}

	@Override
	public void processPacket(INetHandlerLoginServerImplario handler) {
		handler.processClientInfo(this);
	}
}
