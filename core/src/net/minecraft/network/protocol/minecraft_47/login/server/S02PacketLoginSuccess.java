package net.minecraft.network.protocol.minecraft_47.login.server;

import com.mojang.authlib.GameProfile;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.protocol.minecraft_47.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess implements Packet<INetHandlerLoginClient> {

	@Getter
	private GameProfile profile;

	@Getter
	private boolean implario;

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
		String name = decode(buf.readStringFromBuffer(16));
		UUID uuid = UUID.fromString(uuidStr);
		this.profile = new GameProfile(uuid, name);
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		UUID uuid = this.profile.getId();
		buf.writeString(uuid == null ? "" : uuid.toString());
		buf.writeString(encode(this.profile.getName()));
	}

	private String encode(String name) {
		return name.length() < 16 ? name + "*" : name.substring(0, name.length() - 1) + "*";
	}

	private String decode(String name) {
		implario = name.endsWith("*");
		if (implario) {
			System.out.println("Joined Implario server as '" + name + "'");
		}
		return name.replace("*", "");
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerLoginClient handler) {
		handler.handleLoginSuccess(this);
	}

}
