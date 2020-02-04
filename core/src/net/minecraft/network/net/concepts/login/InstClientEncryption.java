package net.minecraft.network.net.concepts.login;

import lombok.Data;
import net.minecraft.network.net.Concept;
import net.minecraft.network.net.ConceptRegistry;
import net.minecraft.network.net.Instance;

@Data
public class InstClientEncryption implements Instance {

	private final byte[] sharedSecret;
	private final byte[] verifyToken;

	public Concept getConcept() {
		return ConceptRegistry.clientEncryption;
	}

}
