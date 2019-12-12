package net.minecraft.network.protocol.minecraft.status;

import net.minecraft.network.protocol.AProtocol;
import net.minecraft.network.protocol.minecraft.status.client.C00PacketServerQuery;
import net.minecraft.network.protocol.minecraft.status.client.C01PacketPing;
import net.minecraft.network.protocol.minecraft.status.server.S00PacketServerInfo;
import net.minecraft.network.protocol.minecraft.status.server.S01PacketPong;

public class ProtocolStatus47 extends AProtocol {
    public ProtocolStatus47() {
        super(1);
        registerPacket(true, C00PacketServerQuery.class, C00PacketServerQuery::new);
        registerPacket(false, S00PacketServerInfo.class, S00PacketServerInfo::new);
        registerPacket(true, C01PacketPing.class, C01PacketPing::new);
        registerPacket(false, S01PacketPong.class, S01PacketPong::new);
    }
}
