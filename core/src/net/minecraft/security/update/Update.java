package net.minecraft.security.update;

import net.minecraft.util.byteable.Decoder;
import net.minecraft.util.byteable.Encoder;
import net.minecraft.util.byteable.SlowDecoder;
import net.minecraft.util.byteable.SlowEncoder;
import net.minecraft.util.crypt.SHA;

import java.util.*;

public class Update {
	public static Update generate(JarFile jar, Encoder newCache, boolean rootUpdate){
		return generate(jar, null, newCache, rootUpdate);
	}

	public static Update generate(JarFile jar, Decoder decoder, boolean rootUpdate){
		return generate(jar, decoder, null, rootUpdate);
	}

	public static Update generate(JarFile jar, Decoder cache, Encoder newCache, boolean rootUpdate){
		Update update = new Update();
		update.rootUpdate = rootUpdate;
		int size = cache == null ? 0 : cache.readInt();
		Map<String, byte[]> hash = new HashMap<>(size);
		Map<String, byte[]> newHash = new HashMap<>();
		for(int i = 0; i < size; i++)
			hash.put(cache.readStr(), cache.readBytes());
		for (Map.Entry<String, byte[]> entry : jar.files.entrySet()) {
			byte lastHash[] = hash.get(entry.getKey());
			byte currentHash[] = SHA.SHA_256(entry.getValue());
			newHash.put(entry.getKey(), currentHash);
			if (lastHash == null || !Arrays.equals(lastHash, currentHash))
				update.addNeedUpdate(entry.getKey(), entry.getValue());
		}
		if(newCache != null) {
			newCache.writeInt(newHash.size());
			for (Map.Entry<String, byte[]> entry : newHash.entrySet())
				newCache.writeString(entry.getKey()).writeBytes(entry.getValue());
		}
		for (String entry : hash.keySet())
			if (jar.files.get(entry) == null)
				update.addRemove(entry);
		return update;
	}

	private final Map<String, byte[]> needUpdate = new HashMap<>();
	private final List<String> needRemove = new ArrayList<>();
	private boolean rootUpdate;

	public void addNeedUpdate(String name, byte array[]) {
		needUpdate.put(name, array);
	}

	public void addRemove(String name) {
		needRemove.add(name);
	}

	public SignedUpdate toSignedUpdate() {
		SlowEncoder encoder = new SlowEncoder();
		encoder.setSizeCompressOfInt(2);
		encoder.setUsingCompressACSII(true);
		encoder.writeInt(needUpdate.size());
		for (Map.Entry<String, byte[]> entry : needUpdate.entrySet())
			encoder.writeString(entry.getKey()).writeBytes(entry.getValue());
		encoder.writeInt(needRemove.size());
		for (String entry : needRemove)
			encoder.writeString(entry);
		return SignedUpdate.fromUpdate(encoder.generate(), rootUpdate);
	}

	Update(byte array[], boolean rootUpdate) {
		this.rootUpdate = rootUpdate;
		SlowDecoder decoder = new SlowDecoder(array);
		decoder.setSizeCompressOfInt(2);
		decoder.setUsingCompressACSII(true);
		int end = decoder.readInt();
		for (int i = 0; i < end; i++)
			needUpdate.put(decoder.readStr(), decoder.readBytes());
		end = decoder.readInt();
		for (int i = 0; i < end; i++)
			needRemove.add(decoder.readStr());
	}

	public Update() {}

	public void writeTo(JarFile edit) {
		for (Map.Entry<String, byte[]> entry : needUpdate.entrySet())
			edit.add(entry.getKey(), entry.getValue());
		for (String str : needRemove)
			edit.remove(str);
	}
}
