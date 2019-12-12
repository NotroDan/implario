package net.minecraft.network.protocol.implario.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.implario.login.client.C02PacketClientInfo;
import net.minecraft.network.protocol.implario.login.server.S04PacketServerInfo;
import net.minecraft.network.protocol.minecraft.login.client.C00PacketLoginStart;
import net.minecraft.network.protocol.minecraft.login.server.S02PacketLoginSuccess;
import net.minecraft.network.protocol.minecraft.login.server.S03PacketEnableCompression;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.chat.ChatComponentText;

public class NetHandlerLoginServer extends net.minecraft.network.protocol.minecraft.login.NetHandlerLoginServer implements INetHandlerLoginServerImplario {
    public NetHandlerLoginServer(MinecraftServer server, NetworkManager networkManager) {
        super(server, networkManager);
    }

    @Override
    public void processLoginStart(C00PacketLoginStart packetIn) {
        this.loginGameProfile = new GameProfile(null, packetIn.getNickname());
        networkManager.sendPacket(new S04PacketServerInfo());
    }

    @Override
    public void processClientInfo(C02PacketClientInfo packetIn) {
        tryAcceptPlayer();
    }
}
