package net.minecraft.network.net;

import net.minecraft.network.PacketBuffer;

public interface ConceptAdapter<I extends Instance> {

	// ToDo: return type
	PacketBuffer adapt(I instance);

}