package net.minecraft.network.net;

import net.minecraft.resources.mapping.Entry;

public class Protocol extends Entry {

	private final ConceptRegistry conceptRegistry;

	public Protocol(ConceptRegistry conceptRegistry, String domain, String name) {
		super(domain, name);
		this.conceptRegistry = conceptRegistry;
	}

	@Override
	public void init() {

	}

}
