package net.minecraft.network.protocol.implario.login;

import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginServer;
import net.minecraft.network.protocol.implario.login.client.C02PacketClientInfo;

public interface INetHandlerLoginServerImplario extends INetHandlerLoginServer {
    void processClientInfo(C02PacketClientInfo packetIn);
}
