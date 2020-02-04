package net.minecraft.network.net;

import net.minecraft.resources.mapping.Registry;

public class ListenerRegistry extends Registry<String, Listener<? extends Instance>> {

	@Override
	public void register(Listener<? extends Instance> mechanic) {
		mechanic.getConcept().getListeners().add((Listener) mechanic);
		super.register(mechanic);
	}

}
