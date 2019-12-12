package net.minecraft.client.network.protocol.implario;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.minecraft_47.play.server.S0EPacketSpawnObject;

public class NetHandlerPlayClient extends net.minecraft.client.network.protocol.minecraft_47.NetHandlerPlayClient {
    public NetHandlerPlayClient(Minecraft mcIn, GuiScreen screen, NetworkManager manager, GameProfile profile) {
        super(mcIn, screen, manager, profile);
    }

    @Override
    public void addEntityToWorld(S0EPacketSpawnObject packetIn, Entity entity) {
        System.out.println("Вау, новый энтити, и я даже могу за этим следить! " + entity);
        super.addEntityToWorld(packetIn, entity);
    }
}
