package net.minecraft.network;

import net.minecraft.network.protocol.minecraft_47.play.server.S08PacketPlayerPosLook;

import java.util.Set;

public interface INetHandlerPlayMPlayer {
    void setPlayerLocation(double x, double y, double z, float yaw, float pitch);

    void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<S08PacketPlayerPosLook.EnumFlags> relativeSet);

    void sendPacket(final Packet packetIn);

    String getRemoteAddress();

    void kickPlayerFromServer(String msg);

    boolean channelOpened();
}
