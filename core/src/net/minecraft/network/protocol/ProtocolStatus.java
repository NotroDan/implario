package net.minecraft.network.protocol;

import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;

public class ProtocolStatus extends Protocol {

	public ProtocolStatus() {
		super(1);
		this.registerPacket(true, C00PacketServerQuery.class, C00PacketServerQuery::new);
		this.registerPacket(false, S00PacketServerInfo.class, S00PacketServerInfo::new);
		this.registerPacket(true, C01PacketPing.class, C01PacketPing::new);
		this.registerPacket(false, S01PacketPong.class, S01PacketPong::new);
	}

}
