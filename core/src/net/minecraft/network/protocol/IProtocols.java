package net.minecraft.network.protocol;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.protocol.minecraft_47.login.INetHandlerLoginClient;
import net.minecraft.network.protocol.minecraft_47.login.INetHandlerLoginServer;
import net.minecraft.network.protocol.minecraft_47.status.INetHandlerStatusClient;
import net.minecraft.network.protocol.minecraft_47.status.INetHandlerStatusServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public interface IProtocols {
    INetHandlerLoginClient getLoginClient();

    INetHandlerLoginServer getLoginServer(MinecraftServer server, NetworkManager manager);

    INetHandlerStatusClient getStatusClient();

    INetHandlerStatusServer getStatusServer(MinecraftServer server, NetworkManager manager);

    IProtocol getProtocolLogin();

    IProtocol getProtocolStatus();

    Packet getDisconnectPacket(IChatComponent component);
}
