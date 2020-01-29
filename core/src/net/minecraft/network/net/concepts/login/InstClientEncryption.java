package net.minecraft.network.net.concepts.login;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.net.Packet;

@Data
@EqualsAndHashCode (callSuper = false)
public class InstClientEncryption extends Packet {

	private final byte[] sharedSecret;
	private final byte[] verifyToken;

}
