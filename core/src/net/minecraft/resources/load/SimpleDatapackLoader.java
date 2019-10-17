package net.minecraft.resources.load;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Datapack;

@RequiredArgsConstructor
public class SimpleDatapackLoader extends DatapackLoader {

	@Getter
	private final Datapack datapack;

	@Override
	public Datapack load(String name, String clientName) {
		try {
			datapack.clientSide = Class.forName(clientName);
		} catch (ClassNotFoundException ignored) {}
		return datapack;
	}

	@Override
	public byte[] read(String name) {
		throw new UnsupportedOperationException("Unable to read resource from simple datapack. Requested resource is '" + name + "'");
	}

	@Override
	public String getName() {
		return datapack.getDomain().getAddress();
	}

	@Override
	public void close() {}

}
