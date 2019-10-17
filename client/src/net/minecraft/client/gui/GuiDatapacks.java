package net.minecraft.client.gui;

import net.minecraft.client.resources.ClientSideDatapack;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Datapacks;
import net.minecraft.resources.load.DatapackLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiDatapacks extends GuiScreen {

	private final Map<String, Datapack> map = new HashMap<>();

	@Override
	public void initGui() {
		List<DatapackLoader> datapacks = Datapacks.getLoaders();
		for (DatapackLoader loader : datapacks) {
			Datapack datapack = loader.get();
			String msg = "§a" + datapack.getDomain() + "§f: " + (datapack.clientSide instanceof ClientSideDatapack ? "client" : "server") + "-side datapack '§e" +
					loader.getName() + "§f', loaded at §7" + loader.getLoadedAt();
			map.put(msg, datapack);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int y = 0;
		for (String s : map.keySet()) drawString(fontRendererObj, s, 10, y += 10, -1);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
