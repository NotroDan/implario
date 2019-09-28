package net.minecraft.network;

import net.minecraft.entity.player.MPlayer;
import net.minecraft.entity.player.Player;
import net.minecraft.resources.event.Listenable;
import net.minecraft.util.byteable.Encoder;

import java.io.IOException;

public interface Packet<T extends INetHandler> extends Listenable {
	/**
	 * Reads the raw packet data from the data stream.
	 */
	void readPacketData(PacketBuffer buf) throws IOException;

	/**
	 * Writes the raw packet data to the data stream.
	 */
	void writePacketData(PacketBuffer buf) throws IOException;

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	void processPacket(T handler);


	default void minecraft_netty(Player player){}

	default void startSend(NetworkManager manager){}

	default void endSend(NetworkManager manager){}
}
