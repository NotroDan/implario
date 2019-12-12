package net.minecraft.network.protocol.minecraft.login.server;

import com.mojang.authlib.GameProfile;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.minecraft.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess implements Packet<INetHandlerLoginClient> {
	@Getter
	private GameProfile profile;

	public S02PacketLoginSuccess() {
	}

	public S02PacketLoginSuccess(GameProfile profileIn) {
		this.profile = profileIn;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		String uuidStr = buf.readStringFromBuffer(36);
		String name = buf.readStringFromBuffer(16);
		UUID uuid = UUID.fromString(uuidStr);
		this.profile = new GameProfile(uuid, name);
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		UUID uuid = this.profile.getId();
		buf.writeString(uuid == null ? "" : uuid.toString());
		buf.writeString(profile.getName());
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerLoginClient handler) {
		handler.handleLoginSuccess(this);
	}

}
