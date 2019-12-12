package net.minecraft.client.network.protocol.minecraft_47;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.protocol.IProtocolsClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.minecraft_47.Protocol47;
import net.minecraft.network.protocol.minecraft_47.play.INetHandlerPlayClient;

public class Protocol47Client extends Protocol47 implements IProtocolsClient {
    public static final IProtocolsClient protocol = new Protocol47Client();

    @Override
    public INetHandlerPlayClient getPlayClient(Minecraft mcIn, GuiScreen screen, NetworkManager manager, GameProfile profile) {
        return new NetHandlerPlayClient(mcIn, screen, manager, profile);
    }
}
