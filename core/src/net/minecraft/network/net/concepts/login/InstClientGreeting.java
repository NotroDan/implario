package net.minecraft.network.net.concepts.login;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.net.Packet;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class InstClientGreeting extends Packet {

	private final String name;
	private final Map<String, String> datapackVersions;

}
