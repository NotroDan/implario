package net.minecraft.security.update;

import __google_.util.ByteUnzip;
import __google_.util.ByteZip;
import net.minecraft.util.crypt.ECDSA;
import net.minecraft.util.crypt.SHA;

import java.math.BigInteger;
import java.util.Arrays;

public class ManifestUpdate {

	private byte[] hashed;
	private byte[] files;

	public ManifestUpdate(byte array[]) {
		ByteUnzip unzip = new ByteUnzip(array);
		hashed = unzip.getBytes();
		files = unzip.getBytes();
	}

	private ManifestUpdate() {}

	static ManifestUpdate fromStructManifest(byte files[]) {
		ManifestUpdate manifestUpdate = new ManifestUpdate();
		manifestUpdate.files = files;
		return manifestUpdate;
	}

	public boolean check() {
		return false;
		//return Arrays.equals(RSA.PUBLIC_KEY.encrypt(new BigInteger(hashed)).toByteArray(), getHash().toByteArray());
	}

	public byte[] toByteArray() {
		return new ByteZip().add(hashed).add(files).build();
	}

	private byte[] getHash() {
		return SHA.SHA_256(files);
	}

	public void verify(ECDSA privateKey) {
		hashed = privateKey.signature(getHash());
	}

	public void setFiles(byte array[]) {
		this.files = array;
	}

}
