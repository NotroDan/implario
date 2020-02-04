package net.minecraft.network.protocol.minecraft_47.play.server;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.minecraft_47.play.INetHandlerPlayClient;

public class S3APacketTabComplete implements Packet<INetHandlerPlayClient> {
	@Getter
	private Collection<String> matches;

	public S3APacketTabComplete() {}

	public S3APacketTabComplete(Collection<String> matchesIn) {
		this.matches = matchesIn;
	}

	public void readPacketData(PacketBuffer buf) throws IOException {
		int size = buf.readVarIntFromBuffer();
		this.matches = new ArrayList<>(size);

		for (int i = 0; i < size; ++i)
			matches.add(buf.readStringFromBuffer(32767));
	}

	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeVarIntToBuffer(matches.size());

		for (String s : matches)
			buf.writeString(s);
	}

	public void processPacket(INetHandlerPlayClient handler) {
		handler.handleTabComplete(this);
	}
}
