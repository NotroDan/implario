package net.minecraft.network.net.concepts.login;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.net.Packet;

@Data
@EqualsAndHashCode (callSuper = true)
public class InstServerCompression extends Packet {

	private final int threshold;

}
