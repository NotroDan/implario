package net.minecraft.network.protocol.implario.login;

import net.minecraft.network.protocol.implario.login.client.C02PacketClientInfo;
import net.minecraft.network.protocol.implario.login.server.S04PacketServerInfo;
import net.minecraft.network.protocol.minecraft.login.ProtocolLogin;

public class ProtocolLoginImplario extends ProtocolLogin {
    public ProtocolLoginImplario(){
        registerPacket(true, C02PacketClientInfo.class, C02PacketClientInfo::new);
        registerPacket(false, S04PacketServerInfo.class, S04PacketServerInfo::new);
    }


}
