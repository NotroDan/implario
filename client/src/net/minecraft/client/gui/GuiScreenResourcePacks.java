package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.Utils;
import net.minecraft.Logger;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.resources.*;
import net.minecraft.client.settings.Settings;
import net.minecraft.util.Util;
import org.lwjgl.Sys;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiScreenResourcePacks extends GuiScreen {

	private static final Logger logger = Logger.getInstance();
	private final GuiScreen parentScreen;
	private List<ResourcePackListEntry> availableResourcePacks;
	private List<ResourcePackListEntry> selectedResourcePacks;

	private GuiResourcePackAvailable availableResourcePacksList;

	private GuiResourcePackSelected selectedResourcePacksList;
	private boolean changed = false;

	public GuiScreenResourcePacks(GuiScreen parentScreenIn) {
		parentScreen = parentScreenIn;
	}

	@Override
	public void initGui() {
		int cacheWidth = width >> 1, cacheHeight = height - 48;
		buttonList.add(new GuiButton(2, cacheWidth - 154, cacheHeight, 150, 20, Lang.format("resourcePack.openFolder")));
		buttonList.add(new GuiButton(1, cacheWidth + 4, cacheHeight, 150, 20, Lang.format("gui.done")));

		if (!changed) {
			availableResourcePacks = new ArrayList<>();
			selectedResourcePacks = new ArrayList<>();
			ResourcePackRepository resourcepackrepository = mc.getResourcePackRepository();
			resourcepackrepository.updateRepositoryEntriesAll();
			List<ResourcePackRepository.Entry> list = Lists.newArrayList(resourcepackrepository.getRepositoryEntriesAll());
			list.removeAll(resourcepackrepository.getRepositoryEntries());

			for (ResourcePackRepository.Entry resourcepackrepository$entry : list)
				availableResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry));

			for (ResourcePackRepository.Entry resourcepackrepository$entry1 : Lists.reverse(resourcepackrepository.getRepositoryEntries()))
				selectedResourcePacks.add(new ResourcePackListEntryFound(this, resourcepackrepository$entry1));

			selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
		}

		availableResourcePacksList = new GuiResourcePackAvailable(mc, 200, height, availableResourcePacks);
		availableResourcePacksList.setSlotXBoundsFromLeft(cacheWidth - 204);
		availableResourcePacksList.registerScrollButtons(7, 8);
		selectedResourcePacksList = new GuiResourcePackSelected(mc, 200, height, selectedResourcePacks);
		selectedResourcePacksList.setSlotXBoundsFromLeft(cacheWidth + 4);
		selectedResourcePacksList.registerScrollButtons(7, 8);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		selectedResourcePacksList.handleMouseInput();
		availableResourcePacksList.handleMouseInput();
	}

	public boolean hasResourcePackEntry(ResourcePackListEntry resourcePack) {
		return selectedResourcePacks.contains(resourcePack);
	}

	public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry resourcePack) {
		return hasResourcePackEntry(resourcePack) ? selectedResourcePacks : availableResourcePacks;
	}

	public List<ResourcePackListEntry> getAvailableResourcePacks() {
		return availableResourcePacks;
	}

	public List<ResourcePackListEntry> getSelectedResourcePacks() {
		return selectedResourcePacks;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled)
			if (button.id == 2) {
				File file1 = mc.getResourcePackRepository().getDirResourcepacks();
				String s = file1.getAbsolutePath();

				if (Util.getOSType() == Util.OS.OSX)
					try {
						logger.info(s);
						Runtime.getRuntime().exec(new String[] {"/usr/bin/open", s});
						return;
					} catch (IOException ioexception1) {
						logger.error("Couldn\'t open file", ioexception1);
					}
				else if (Util.getOSType() == Util.OS.WINDOWS) {
					String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", s);

					try {
						Runtime.getRuntime().exec(s1);
						return;
					} catch (IOException ioexception) {
						logger.error("Couldn\'t open file", ioexception);
					}
				}

				boolean flag = false;

				try {
					Class<?> oclass = Class.forName("java.awt.Desktop");
					Object object = oclass.getMethod("getDesktop", Utils.CLASS).invoke(null);
					oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, file1.toURI());
				} catch (Throwable throwable) {
					logger.error("Couldn\'t open link", throwable);
					flag = true;
				}

				if (flag) {
					logger.info("Opening via system class!");
					Sys.openURL("file://" + s);
				}
			} else if (button.id == 1) {
				if (changed) {
					List<ResourcePackRepository.Entry> list = new ArrayList<>();

					for (ResourcePackListEntry resourcepacklistentry : this.selectedResourcePacks)
						if (resourcepacklistentry instanceof ResourcePackListEntryFound)
							list.add(((ResourcePackListEntryFound) resourcepacklistentry).func_148318_i());

					Collections.reverse(list);
					mc.getResourcePackRepository().setRepositories(list);
					Settings.resourcePacks.clear();
					Settings.incompatibleResourcePacks.clear();

					for (ResourcePackRepository.Entry resourcepackrepository$entry : list) {
						Settings.resourcePacks.add(resourcepackrepository$entry.getResourcePackName());

						if (resourcepackrepository$entry.func_183027_f() != 1)
							Settings.incompatibleResourcePacks.add(resourcepackrepository$entry.getResourcePackName());
					}

					Settings.saveOptions();
					mc.refreshResources();
				}

				mc.displayGuiScreen(parentScreen);
			}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
		selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawBackground(0);
		availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
		selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
		int cacheWidth = width >> 1;
		drawCenteredString(fontRendererObj, Lang.format("resourcePack.title"), cacheWidth, 16, 16777215);
		drawCenteredString(fontRendererObj, Lang.format("resourcePack.folderInfo"), cacheWidth - 77, height - 26, 8421504);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void markChanged() {
		changed = true;
	}

}
