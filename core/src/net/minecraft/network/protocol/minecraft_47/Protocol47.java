package net.minecraft.network.protocol.minecraft_47;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.protocol.*;
import net.minecraft.network.protocol.minecraft_47.handshake.ProtocolHandshaking47;
import net.minecraft.network.protocol.minecraft_47.login.INetHandlerLoginClient;
import net.minecraft.network.protocol.minecraft_47.login.INetHandlerLoginServer;
import net.minecraft.network.protocol.minecraft_47.login.ProtocolLogin47;
import net.minecraft.network.protocol.minecraft_47.login.server.S00PacketDisconnect;
import net.minecraft.network.protocol.minecraft_47.play.ProtocolPlay47;
import net.minecraft.network.protocol.minecraft_47.status.INetHandlerStatusClient;
import net.minecraft.network.protocol.minecraft_47.status.INetHandlerStatusServer;
import net.minecraft.network.protocol.minecraft_47.status.ProtocolStatus47;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.protocol.minecraft_47.login.NetHandlerLoginServer;
import net.minecraft.network.protocol.minecraft_47.status.NetHandlerStatusServer;
import net.minecraft.util.IChatComponent;

public class Protocol47 implements IProtocols{
    public static final IProtocols protocol = new Protocol47();

    public static final IProtocol HANDSHAKING = new ProtocolHandshaking47();
    public static final IProtocol PLAY = new ProtocolPlay47();
    public static final IProtocol LOGIN = new ProtocolLogin47();
    public static final IProtocol STATUS = new ProtocolStatus47();

    @Override
    public INetHandlerLoginClient getLoginClient() {
        return null;
    }

    @Override
    public INetHandlerLoginServer getLoginServer(MinecraftServer server, NetworkManager manager) {
        return new NetHandlerLoginServer(server, manager);
    }

    @Override
    public INetHandlerStatusClient getStatusClient() {
        return null;
    }

    @Override
    public INetHandlerStatusServer getStatusServer(MinecraftServer server, NetworkManager manager) {
        return new NetHandlerStatusServer(server, manager);
    }

    @Override
    public IProtocol getProtocolLogin() {
        return LOGIN;
    }

    @Override
    public IProtocol getProtocolStatus() {
        return STATUS;
    }

    @Override
    public Packet getDisconnectPacket(IChatComponent component) {
        return new S00PacketDisconnect(component);
    }
}
