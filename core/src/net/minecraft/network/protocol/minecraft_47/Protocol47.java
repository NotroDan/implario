package net.minecraft.network.protocol.minecraft_47;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.*;
import net.minecraft.network.protocol.minecraft.ProtocolMinecraft;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginServer;
import net.minecraft.network.protocol.minecraft_47.play.INetHandlerPlayServer;
import net.minecraft.network.protocol.minecraft_47.play.ProtocolPlay47;
import net.minecraft.server.MinecraftServer;

public class Protocol47 extends ProtocolMinecraft {
    public static final IProtocols protocol = new Protocol47();

    private static final IProtocol PLAY = new ProtocolPlay47();

    @Override
    public IProtocol getProtocolPlay() {
        return PLAY;
    }
}
