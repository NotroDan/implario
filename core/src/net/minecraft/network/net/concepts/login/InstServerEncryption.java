package net.minecraft.network.net.concepts.login;

import lombok.Data;
import net.minecraft.network.net.Concept;
import net.minecraft.network.net.ConceptRegistry;
import net.minecraft.network.net.Instance;

@Data
public class InstServerEncryption implements Instance {

	private final String serverId;
	private final byte[] publicKey;
	private final byte[] verifyToken;

	public Concept getConcept() {
		return ConceptRegistry.serverEncryption;
	}

}
