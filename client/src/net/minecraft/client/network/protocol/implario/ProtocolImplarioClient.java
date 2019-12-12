package net.minecraft.client.network.protocol.implario;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.protocol.IProtocolsClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.implario.ProtocolImplario;
import net.minecraft.network.protocol.minecraft_47.play.INetHandlerPlayClient;

public class ProtocolImplarioClient extends ProtocolImplario implements IProtocolsClient {
    public static final IProtocolsClient protocol = new ProtocolImplarioClient();

    @Override
    public INetHandlerPlayClient getPlayClient(Minecraft mcIn, GuiScreen screen, NetworkManager manager, GameProfile profile) {
        return new NetHandlerPlayClient(mcIn, screen, manager, profile);
    }
}
