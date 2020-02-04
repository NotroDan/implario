package net.minecraft.network.net.concepts.login;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.net.Concept;
import net.minecraft.network.net.ConceptRegistry;
import net.minecraft.network.net.Packet;

@Data
public class InstServerCompression implements Packet {

	private final int threshold;

	public Concept getConcept() {
		return ConceptRegistry.serverCompression;
	}

}
