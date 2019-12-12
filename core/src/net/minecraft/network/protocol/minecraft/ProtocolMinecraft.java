package net.minecraft.network.protocol.minecraft;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.protocol.IProtocol;
import net.minecraft.network.protocol.IProtocols;
import net.minecraft.network.protocol.minecraft.handshake.IProtocolHandshake;
import net.minecraft.network.protocol.minecraft.handshake.ProtocolHandshaking;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginServer;
import net.minecraft.network.protocol.minecraft.login.IProtocolLogin;
import net.minecraft.network.protocol.minecraft.login.NetHandlerLoginServer;
import net.minecraft.network.protocol.minecraft.login.ProtocolLogin;
import net.minecraft.network.protocol.minecraft.login.server.S00PacketDisconnect;
import net.minecraft.network.protocol.minecraft.status.INetHandlerStatusServer;
import net.minecraft.network.protocol.minecraft.status.NetHandlerStatusServer;
import net.minecraft.network.protocol.minecraft.status.ProtocolStatus47;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public abstract class ProtocolMinecraft implements IProtocols {
    public static final IProtocolHandshake HANDSHAKE = new ProtocolHandshaking();
    private static final IProtocolLogin LOGIN = new ProtocolLogin();
    private static final IProtocol STATUS = new ProtocolStatus47();

    @Override
    public IProtocolHandshake getProtocolHandshake() {
        return HANDSHAKE;
    }

    @Override
    public IProtocolLogin getProtocolLogin() {
        return LOGIN;
    }

    @Override
    public IProtocol getProtocolStatus() {
        return STATUS;
    }

    @Override
    public INetHandlerLoginServer getLoginServer(MinecraftServer server, NetworkManager manager) {
        return new NetHandlerLoginServer(server, manager);
    }

    @Override
    public INetHandlerStatusServer getStatusServer(MinecraftServer server, NetworkManager manager) {
        return new NetHandlerStatusServer(server, manager);
    }

    @Override
    public Packet getDisconnectPacket(IChatComponent component) {
        return new S00PacketDisconnect(component);
    }
}
