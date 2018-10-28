package net.minecraft;

import __google_.crypt.async.RSA;
import __google_.net.Response;
import __google_.net.client.Client;
import __google_.util.ByteZip;
import __google_.util.Byteable;
import __google_.util.FileIO;
import net.minecraft.client.Minecraft;

public class Auth {

	public static RSA password;

	public static void setPassword(String pass, int rounds){
		password = RSA.generate(pass, 2048, rounds);
		savePassword();
	}

	public static void loadPassword(){
		byte read[] = FileIO.readBytes("rsa.key");
		if(read == null)return;
		password = Byteable.toByteable(read, RSA.class);
	}

	public static void savePassword(){
		if(password == null)return;
		FileIO.writeBytes("rsa.key", password.toBytes());
	}

	public static void log(){
		Client client = new Client("lmaomc.ru", 1424);
		client.connect();
		client.getCertificate();
		byte decoded[] = Auth.password.decodeByte(client.apply(new Response(2, new ByteZip().add(Minecraft.getGlobalName()).build())).getContent());
		client.apply(new Response(0, new ByteZip().add(Minecraft.getGlobalName()).add(decoded).build()));
		client.close();
	}

	public static void reg(){
		Client client = new Client("lmaomc.ru", 1424);
		client.connect();
		client.getCertificate();
		client.apply(new Response(1, new ByteZip().add(Minecraft.getGlobalName()).add(Auth.password.getBytePublicKey()).build()));
		client.close();
	}

}
