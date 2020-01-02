package net.minecraft.client.network.protocol.implario;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.NetworkManager;

public class NetHandlerPlayClient extends net.minecraft.client.network.protocol.minecraft_47.NetHandlerPlayClient {
    public NetHandlerPlayClient(Minecraft mcIn, GuiScreen screen, NetworkManager manager, GameProfile profile) {
        super(mcIn, screen, manager, profile);
    }
}
