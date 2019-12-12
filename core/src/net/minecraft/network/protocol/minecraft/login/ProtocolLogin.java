package net.minecraft.network.protocol.minecraft.login;

import net.minecraft.network.Packet;
import net.minecraft.network.protocol.AProtocol;
import net.minecraft.network.protocol.minecraft.login.client.C00PacketLoginStart;
import net.minecraft.network.protocol.minecraft.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.protocol.minecraft.login.server.S00PacketDisconnect;
import net.minecraft.network.protocol.minecraft.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.protocol.minecraft.login.server.S02PacketLoginSuccess;
import net.minecraft.network.protocol.minecraft.login.server.S03PacketEnableCompression;

public class ProtocolLogin extends AProtocol implements IProtocolLogin {
	public ProtocolLogin() {
		super(2);
		registerPacket(false, S00PacketDisconnect.class, S00PacketDisconnect::new);
		registerPacket(false, S01PacketEncryptionRequest.class, S01PacketEncryptionRequest::new);
		registerPacket(false, S02PacketLoginSuccess.class, S02PacketLoginSuccess::new);
		registerPacket(false, S03PacketEnableCompression.class, S03PacketEnableCompression::new);
		registerPacket(true, C00PacketLoginStart.class, C00PacketLoginStart::new);
		registerPacket(true, C01PacketEncryptionResponse.class, C01PacketEncryptionResponse::new);
	}

	@Override
	public Packet<INetHandlerLoginServer> getLoginStart(String nickname) {
		return new C00PacketLoginStart(nickname);
	}
}
