package net.minecraft.network.net.concepts.login;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.net.Packet;

@Data
@EqualsAndHashCode (callSuper = false)
public class InstServerEncryption extends Packet {

	private final String serverId;
	private final byte[] publicKey;
	private final byte[] verifyToken;

}
