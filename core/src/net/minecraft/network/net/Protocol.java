package net.minecraft.network.net;

import lombok.Getter;
import net.minecraft.resources.mapping.Mechanic;

@Getter
public class Protocol extends Mechanic<String> {

	private final ConceptRegistry conceptRegistry;

	public Protocol(ConceptRegistry conceptRegistry, String domain, String name) {
		super(name);
		this.conceptRegistry = conceptRegistry;
	}

	@Override
	public void init() {

	}

}
