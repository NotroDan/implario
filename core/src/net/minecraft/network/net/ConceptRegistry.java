package net.minecraft.network.net;

import net.minecraft.network.net.concepts.login.InstClientEncryption;
import net.minecraft.network.net.concepts.login.InstClientGreeting;
import net.minecraft.network.net.concepts.login.InstServerCompression;
import net.minecraft.network.net.concepts.login.InstServerEncryption;
import net.minecraft.resources.mapping.Registry;

public class ConceptRegistry extends Registry<Concept> {

	public static final Concept<InstServerEncryption> serverEncryption = new Concept<>(true);
	public static final Concept<InstServerCompression> serverCompression = new Concept<>(true);

	public static final Concept<InstClientEncryption> clientEncryption = new Concept<>(false);
	public static final Concept<InstClientGreeting> clientGreeting = new Concept<>(false);

}
