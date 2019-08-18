package net.minecraft.security.update;

import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.crypt.ECDSA;
import net.minecraft.util.crypt.SHA;
import net.minecraft.util.crypt.TimedSertificate;

public class SignedUpdate {
	private byte[] hashed;
	private byte[] files;
	private boolean rootUpdate;

	public SignedUpdate(Decoder decoder) {
		hashed = decoder.readBytes();
		files = decoder.readBytes();
		rootUpdate = decoder.readBoolean();
	}

	private SignedUpdate() {}

	static SignedUpdate fromStructManifest(byte files[], boolean rootUpdate) {
		SignedUpdate signedUpdate = new SignedUpdate();
		signedUpdate.files = files;
		signedUpdate.rootUpdate = rootUpdate;
		return signedUpdate;
	}

	public boolean check() {
		return ((TimedSertificate)(rootUpdate ? SecurityKeys.rootKey :
				SecurityKeys.sertificate)).getSert().verify(getHash(), hashed);
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
