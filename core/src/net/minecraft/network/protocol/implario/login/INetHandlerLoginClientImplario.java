package net.minecraft.network.protocol.implario.login;

import net.minecraft.network.protocol.implario.login.server.S04PacketServerInfo;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginClient;

public interface INetHandlerLoginClientImplario extends INetHandlerLoginClient {
    void processServerInfo(S04PacketServerInfo serverInfo);
}
