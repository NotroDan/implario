package net.minecraft.resources.update;

import __google_.util.ByteUnzip;
import __google_.util.ByteZip;

import java.math.BigInteger;
import java.util.Arrays;

public class ManifestUpdate {
    private byte[] hashed;
    private byte[] files;

    public ManifestUpdate(byte array[]){
        ByteUnzip unzip = new ByteUnzip(array);
        hashed = unzip.getBytes();
        files = unzip.getBytes();
    }

    private ManifestUpdate(){}

    static ManifestUpdate fromStructManifest(byte files[]){
        ManifestUpdate manifestUpdate = new ManifestUpdate();
        manifestUpdate.files = files;
        return manifestUpdate;
    }

    public boolean check(){
        return Arrays.equals(RSA.PUBLIC_KEY.encrypt(new BigInteger(hashed)).toByteArray(), getHash().toByteArray());
    }

    public byte[] toByteArray(){
        return new ByteZip().add(hashed).add(files).build();
    }

    private BigInteger getHash(){
        return new BigInteger(RSA.hashing(files));
    }

    public void verify(RSA privateKey){
        hashed = privateKey.decrypt(getHash()).toByteArray();
    }

    public void setFiles(byte array[]){
        this.files = array;
    }
}
