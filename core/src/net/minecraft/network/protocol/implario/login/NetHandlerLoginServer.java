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
import org.apache.commons.lang3.Validate;

public class NetHandlerLoginServer extends net.minecraft.network.protocol.minecraft.login.NetHandlerLoginServer implements INetHandlerLoginServerImplario {

	private C02PacketClientInfo infoPacket;

	public NetHandlerLoginServer(MinecraftServer server, NetworkManager networkManager) {
        super(server, networkManager);
    }

    @Override
    public void processLoginStart(C00PacketLoginStart packetIn) {
        this.loginGameProfile = new GameProfile(null, packetIn.getNickname());
        networkManager.sendPacket(new S04PacketServerInfo());
    }

	@Override
	public void update() {
		if (infoPacket != null) tryAcceptPlayer();
		else super.update();
	}

	@Override
    public void processClientInfo(C02PacketClientInfo packetIn) {
		Validate.validState(infoPacket == null, "Info packet sent twice");
		this.infoPacket = packetIn;
    }
}
