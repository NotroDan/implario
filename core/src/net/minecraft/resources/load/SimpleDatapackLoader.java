package net.minecraft.resources.load;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.logging.Log;
import net.minecraft.resources.Datapack;

@RequiredArgsConstructor
@Getter
public class SimpleDatapackLoader extends DatapackLoader {

	public SimpleDatapackLoader(Datapack datapack, DatapackInfo info) {
		this.datapack = datapack;
		this.properties = info;
	}

	@Override
	public Datapack createInstance() {
		String clientMainClass = properties.getClientMain();
		if (clientMainClass != null) try {
			datapack.clientSide = Class.forName(clientMainClass);
		} catch (ClassNotFoundException ignored) {
			Log.MAIN.warn("SimpleDPL '" + getName() + "' has broken main client class '" + clientMainClass + "'");
		}
		return datapack;
	}

	@Override
	public DatapackInfo prepareReader() {
		return properties;
	}

	@Override
	public String getName() {
		return datapack.getDomain().getAddress();
	}

	@Override
	public void close() {}

}
