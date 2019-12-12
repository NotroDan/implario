package net.minecraft.network.protocol.minecraft.handshake;

import net.minecraft.network.INetHandler;

public interface INetHandlerHandshakeServer extends INetHandler {
	/**
	 * There are two recognized intentions for initiating a handshake: logging in and acquiring server status. The
	 * NetworkManager's protocol will be reconfigured according to the specified intention, although a login-intention
	 * must pass a versioncheck or receive a disconnect otherwise
	 */
	void processHandshake(C00Handshake packetIn);
}
