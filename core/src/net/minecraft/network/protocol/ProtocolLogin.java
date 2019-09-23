package net.minecraft.network.protocol;

import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;

public class ProtocolLogin extends Protocol {

	public ProtocolLogin() {
		super(2);
		this.registerPacket(false, S00PacketDisconnect.class, S00PacketDisconnect::new);
		this.registerPacket(false, S01PacketEncryptionRequest.class, S01PacketEncryptionRequest::new);
		this.registerPacket(false, S02PacketLoginSuccess.class, S02PacketLoginSuccess::new);
		this.registerPacket(false, S03PacketEnableCompression.class, S03PacketEnableCompression::new);
		this.registerPacket(true, C00PacketLoginStart.class, C00PacketLoginStart::new);
		this.registerPacket(true, C01PacketEncryptionResponse.class, C01PacketEncryptionResponse::new);
	}

}
