package net.minecraft.network.protocol.minecraft.login.client;

import com.mojang.authlib.GameProfile;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginServer;

public class C00PacketLoginStart implements Packet<INetHandlerLoginServer> {
	@Getter
	private String nickname;

	public C00PacketLoginStart() {}

	public C00PacketLoginStart(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException {
		nickname = buf.readStringFromBuffer(16);
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeString(nickname);
	}

	@Override
	public void processPacket(INetHandlerLoginServer handler) {
		handler.processLoginStart(this);
	}
}
