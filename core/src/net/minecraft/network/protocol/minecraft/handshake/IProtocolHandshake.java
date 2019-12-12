package net.minecraft.network.protocol.minecraft.handshake;

import net.minecraft.network.Packet;
import net.minecraft.network.protocol.IProtocol;

public interface IProtocolHandshake extends IProtocol {
    Packet<INetHandlerHandshakeServer> getHandshake(int version, boolean requireStatus);
}
