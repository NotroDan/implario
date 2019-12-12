package net.minecraft.network.protocol.implario;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.implario.login.NetHandlerLoginServer;
import net.minecraft.network.protocol.implario.login.ProtocolLoginImplario;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginServer;
import net.minecraft.network.protocol.minecraft.login.IProtocolLogin;
import net.minecraft.network.protocol.minecraft_47.Protocol47;
import net.minecraft.server.MinecraftServer;

public class ProtocolImplario extends Protocol47 {
    public static final ProtocolImplario protocol = new ProtocolImplario();

    private static final IProtocolLogin LOGIN = new ProtocolLoginImplario();

    @Override
    public IProtocolLogin getProtocolLogin() {
        return LOGIN;
    }

    @Override
    public INetHandlerLoginServer getLoginServer(MinecraftServer server, NetworkManager manager) {
        return new NetHandlerLoginServer(server, manager);
    }
}
