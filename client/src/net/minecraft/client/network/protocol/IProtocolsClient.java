package net.minecraft.client.network.protocol;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.IProtocols;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginClient;
import net.minecraft.network.protocol.minecraft_47.play.INetHandlerPlayClient;
import net.minecraft.network.protocol.minecraft_47.play.INetHandlerPlayServer;

public interface IProtocolsClient extends IProtocols {
    INetHandlerPlayClient getPlayClient(Minecraft mcIn, GuiScreen screen, NetworkManager manager, GameProfile profile);
}
