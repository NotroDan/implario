package net.minecraft.network.net;

import lombok.Getter;
import lombok.ToString;
import net.minecraft.resources.mapping.Mechanic;

import java.util.List;

@Getter
@ToString
public class Concept<Type extends Instance> extends Mechanic<String> {

	private final boolean isServerSide;
	private ConceptAdapter<Type>[] adapters;
	private List<Listener<Type>> listeners;

	public Concept(String name, boolean isServerSide) {
		super(name);
		this.isServerSide = isServerSide;
	}

	public void deploy(Connection connection, Type instance) {
		ConceptAdapter<Type> adapter = adapters[connection.getProtocolId()];
		Object packet = adapter.adapt(instance);
		connection.send(packet);
	}

}
