package net.minecraft.network.net;

import lombok.Getter;
import net.minecraft.resources.mapping.Mechanic;

@Getter
public class Listener<T extends Instance> extends Mechanic<String> {

	private final Concept<T> concept;
	private final Handler handler;

	public Listener(String name, Concept<T> concept, Handler handler) {
		super(name);
		this.concept = concept;
		this.handler = handler;
	}

	@FunctionalInterface
	public interface Handler {
		void handle(Connection connection, Instance instance);
	}

}
