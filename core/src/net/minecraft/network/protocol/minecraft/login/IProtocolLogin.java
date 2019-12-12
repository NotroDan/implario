package net.minecraft.network.protocol.minecraft.login;

import net.minecraft.network.Packet;
import net.minecraft.network.protocol.IProtocol;

public interface IProtocolLogin extends IProtocol {
    Packet<INetHandlerLoginServer> getLoginStart(String nickname);
}
