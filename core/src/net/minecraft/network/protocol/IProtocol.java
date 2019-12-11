package net.minecraft.network.protocol;

import net.minecraft.network.Packet;

public interface IProtocol {
    Packet getPacket(boolean isClientSide, int id);

    int getPacketID(boolean isClientSide, Packet packet);
}
