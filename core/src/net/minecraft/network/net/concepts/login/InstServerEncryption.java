package net.minecraft.network.net.concepts.login;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.net.Concept;
import net.minecraft.network.net.ConceptRegistry;
import net.minecraft.network.net.Packet;

@Data
public class InstServerEncryption implements Packet {

	private final String serverId;
	private final byte[] publicKey;
	private final byte[] verifyToken;

	public Concept getConcept() {
		return ConceptRegistry.serverEncryption;
	}

}
