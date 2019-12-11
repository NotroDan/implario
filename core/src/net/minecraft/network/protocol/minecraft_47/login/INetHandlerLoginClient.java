package net.minecraft.network.protocol.minecraft_47.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.protocol.minecraft_47.login.server.S00PacketDisconnect;
import net.minecraft.network.protocol.minecraft_47.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.protocol.minecraft_47.login.server.S02PacketLoginSuccess;
import net.minecraft.network.protocol.minecraft_47.login.server.S03PacketEnableCompression;

public interface INetHandlerLoginClient extends INetHandler {

	void handleEncryptionRequest(S01PacketEncryptionRequest packetIn);

	void handleLoginSuccess(S02PacketLoginSuccess packetIn);

	void handleDisconnect(S00PacketDisconnect packetIn);

	void handleEnableCompression(S03PacketEnableCompression packetIn);

}
