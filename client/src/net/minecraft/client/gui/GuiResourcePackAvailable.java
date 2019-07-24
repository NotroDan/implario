package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.resources.ResourcePackListEntry;

import java.util.List;

public class GuiResourcePackAvailable extends GuiResourcePackList {

	public GuiResourcePackAvailable(Minecraft mcIn, int widthIn, int heightIn, List<ResourcePackListEntry> resourcePackList) {
		super(mcIn, widthIn, heightIn, resourcePackList);
	}

	@Override
	protected String getListHeader() {
		return Lang.format("resourcePack.available.title");
	}

}
