package net.minecraft.network.protocol;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.protocol.minecraft.handshake.IProtocolHandshake;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginClient;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginServer;
import net.minecraft.network.protocol.minecraft.login.IProtocolLogin;
import net.minecraft.network.protocol.minecraft.status.INetHandlerStatusServer;
import net.minecraft.network.protocol.minecraft_47.play.INetHandlerPlayClient;
import net.minecraft.network.protocol.minecraft_47.play.INetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public interface IProtocols {
    IProtocolHandshake getProtocolHandshake();

    IProtocolLogin getProtocolLogin();

    INetHandlerLoginServer getLoginServer(MinecraftServer server, NetworkManager manager);

    IProtocol getProtocolStatus();

    INetHandlerStatusServer getStatusServer(MinecraftServer server, NetworkManager manager);

    IProtocol getProtocolPlay();

    Packet getDisconnectPacket(IChatComponent component);
}
