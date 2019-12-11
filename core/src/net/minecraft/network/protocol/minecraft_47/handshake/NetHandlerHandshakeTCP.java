package net.minecraft.network.protocol.minecraft_47.handshake;

import net.minecraft.network.protocol.IProtocols;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.minecraft_47.handshake.client.C00Handshake;
import net.minecraft.network.protocol.implario.ProtocolImplario;
import net.minecraft.network.protocol.minecraft_47.Protocol47;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class NetHandlerHandshakeTCP implements INetHandlerHandshakeServer {

	private final MinecraftServer server;
	private final NetworkManager networkManager;

	public NetHandlerHandshakeTCP(MinecraftServer serverIn, NetworkManager netManager) {
		this.server = serverIn;
		this.networkManager = netManager;
	}

	/**
	 * There are two recognized intentions for initiating a handshake: logging in and acquiring server status. The
	 * NetworkManager's protocol will be reconfigured according to the specified intention, although a login-intention
	 * must pass a versioncheck or receive a disconnect otherwise
	 */
	public void processHandshake(C00Handshake packetIn) {
		boolean state = packetIn.isRequestedStatus();
		IProtocols protocol = Protocol47.protocol;
		if("Implario".equals(packetIn.getIp()))
			protocol = ProtocolImplario.protocol;
		if (!state) {
			networkManager.setConnectionState(protocol.getProtocolLogin());

			if (packetIn.getProtocolVersion() != 47) {
				ChatComponentText chatcomponenttext = new ChatComponentText("Этот сервер использует протокол §eNotchian 47§f (версия 1.8.8)\n" +
						"§fВаш клиент использует протокол §eNotchian " + packetIn.getProtocolVersion() + "§f, который несовместим с Notchian 47.\n§f\n" +
						"Используйте клиент §eImplario§f для входа на этот сервер.\n§7github.com/DelfikPro/Implario");
				networkManager.sendPacket(protocol.getDisconnectPacket(chatcomponenttext));
				networkManager.closeChannel(chatcomponenttext);
				return;
			}
			networkManager.setNetHandler(protocol.getLoginServer(server, networkManager));
		} else
			networkManager.setNetHandler(protocol.getStatusServer(server, networkManager));
	}

	/**
	 * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
	 */
	public void onDisconnect(IChatComponent reason) {}
}
