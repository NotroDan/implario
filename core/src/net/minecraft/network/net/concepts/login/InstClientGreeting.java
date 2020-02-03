package net.minecraft.network.net.concepts.login;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.net.Concept;
import net.minecraft.network.net.ConceptRegistry;
import net.minecraft.network.net.Packet;

import java.util.Map;

@Data
public class InstClientGreeting implements Packet {

	private final String name;
	private final Map<String, String> datapackVersions;

	public Concept getConcept() {
		return ConceptRegistry.clientGreeting;
	}

}
