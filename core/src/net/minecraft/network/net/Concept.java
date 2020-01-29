package net.minecraft.network.net;

import lombok.Data;
import net.minecraft.resources.mapping.Entry;

@Data
public class Concept<Type extends Packet> extends Entry {

	private final boolean isServerSide;
	private ConceptAdapter<Type>[] adapters;

	public void deploy(Connection connection, Type instance) {
		ConceptAdapter<Type> adapter = adapters[connection.getProtocolId()];
		Object packet = adapter.adapt(instance);
		connection.send(packet);
	}

	public void register

}
