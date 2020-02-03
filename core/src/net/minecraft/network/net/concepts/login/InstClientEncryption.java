package net.minecraft.network.net.concepts.login;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.net.Concept;
import net.minecraft.network.net.ConceptRegistry;
import net.minecraft.network.net.Packet;

@Data
public class InstClientEncryption implements Packet {

	private final byte[] sharedSecret;
	private final byte[] verifyToken;

	public Concept getConcept() {
		return ConceptRegistry.clientEncryption;
	}

}
