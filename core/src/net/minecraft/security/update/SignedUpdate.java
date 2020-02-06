package net.minecraft.security.update;

import lombok.Getter;
import oogle.util.byteable.Decoder;
import oogle.util.byteable.Encoder;
import net.minecraft.util.crypt.ECDSA;
import net.minecraft.util.crypt.SHA;
import net.minecraft.util.crypt.SecurityKey;
import net.minecraft.util.crypt.TimedSertificate;

public class SignedUpdate {
	private byte[] hashed;
	private byte[] files;
	@Getter
	private boolean rootUpdate;

	public SignedUpdate(Decoder decoder) {
		hashed = decoder.readBytes();
		files = decoder.readBytes();
		rootUpdate = decoder.readBoolean();
	}

	private SignedUpdate() {}

	static SignedUpdate fromUpdate(byte files[], boolean rootUpdate) {
		SignedUpdate signedUpdate = new SignedUpdate();
		signedUpdate.files = files;
		signedUpdate.rootUpdate = rootUpdate;
		return signedUpdate;
	}

	public boolean check(ECDSA sert) {
		return sert.verify(getHash(), hashed);
	}

	public boolean check(SecurityKey key){
		if(key == null)return true;
		return key.check(isRootUpdate(), files, hashed);
	}

	public void encode(Encoder encoder){
		encoder.writeBytes(hashed).writeBytes(files).writeBoolean(rootUpdate);
	}

	public Update getUpdate(){
		return new Update(files, rootUpdate);
	}

	private byte[] getHash() {
		return SHA.SHA_256(files);
	}

	public void verify(ECDSA privateKey) {
		hashed = privateKey.signature(getHash());
	}
}
