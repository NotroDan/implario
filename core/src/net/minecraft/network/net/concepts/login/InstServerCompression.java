package net.minecraft.network.net.concepts.login;

import lombok.Data;
import net.minecraft.network.net.Concept;
import net.minecraft.network.net.ConceptRegistry;
import net.minecraft.network.net.Instance;

@Data
public class InstServerCompression implements Instance {

	private final int threshold;

	public Concept getConcept() {
		return ConceptRegistry.serverCompression;
	}

}
