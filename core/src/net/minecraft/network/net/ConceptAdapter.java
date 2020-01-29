package net.minecraft.network.net;

public interface ConceptAdapter<I extends Packet> {

	// ToDo: return type
	Object adapt(I instance);

}