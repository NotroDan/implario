package net.minecraft.network.protocol.minecraft_47.play;

import net.minecraft.network.Packet;
import net.minecraft.network.protocol.minecraft_47.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;

public class PlayServer47 implements IPlayServer {
    @Override
    public Packet<INetHandlerPlayClient> getChatMessage(IChatComponent component) {
        return new S02PacketChat();
    }
}
