package net.minecraft.client.gui;

import net.minecraft.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.resources.Lang;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.AnvilConverterException;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback {

	private static final Logger logger = Logger.getInstance();
	private final DateFormat dateFormat = new SimpleDateFormat();
	protected GuiScreen parentScreen;
	protected String select = "Select world";
	private boolean lock;
	private int selectedWorld;
	private java.util.List<SaveFormatComparator> saveList;
	private GuiSelectWorld.List worlds;
	private String selectWorld;
	private String conversionWorld;
	private String[] gamemodes = new String[4];
	private boolean deleteWorld;
	private GuiButton deleteButton;
	private GuiButton selectButton;
	private GuiButton renameButton;
	private GuiButton recreateButton;

	public GuiSelectWorld(GuiScreen parentScreenIn) {
		parentScreen = parentScreenIn;
	}

	@Override
	public void initGui() {
		select = Lang.format("selectWorld.title");

		try {
			getSaveList();
		} catch (AnvilConverterException anvilconverterexception) {
			logger.error("Couldn\'t load level list", anvilconverterexception);
			this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", anvilconverterexception.getMessage()));
			return;
		}

		selectWorld = Lang.format("selectWorld.world");
		conversionWorld = Lang.format("selectWorld.conversion");
		gamemodes[WorldSettings.GameType.SURVIVAL.getID()] = Lang.format("gameMode.survival");
		gamemodes[WorldSettings.GameType.CREATIVE.getID()] = Lang.format("gameMode.creative");
		gamemodes[WorldSettings.GameType.ADVENTURE.getID()] = Lang.format("gameMode.adventure");
		gamemodes[WorldSettings.GameType.SPECTATOR.getID()] = Lang.format("gameMode.spectator");
		worlds = new GuiSelectWorld.List(mc);
		worlds.registerScrollButtons(4, 5);
		registerButtons();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		worlds.handleMouseInput();
	}

	private void getSaveList() throws AnvilConverterException {
		ISaveFormat isaveformat = mc.worldController.getSaveLoader();
		saveList = isaveformat.getSaveList();
		Collections.sort(saveList);
		selectedWorld = -1;
	}

	private String getFileName(int save) {
		return saveList.get(save).getFileName();
	}

	private String getSaveName(int save) {
		String s = saveList.get(save).getDisplayName();

		if (StringUtils.isEmpty(s)) s = Lang.format("selectWorld.world") + " " + (save + 1);

		return s;
	}

	private void registerButtons() {
		int cacheWidth = width >> 1;
		buttonList.add(selectButton = new GuiButton(1, cacheWidth - 154, height - 52, 150, 20, Lang.format("selectWorld.select")));
		buttonList.add(new GuiButton(3, cacheWidth + 4, height - 52, 150, 20, Lang.format("selectWorld.create")));
		buttonList.add(renameButton = new GuiButton(6, cacheWidth - 154, height - 28, 72, 20, Lang.format("selectWorld.rename")));
		buttonList.add(deleteButton = new GuiButton(2, cacheWidth - 76, height - 28, 72, 20, Lang.format("selectWorld.delete")));
		buttonList.add(recreateButton = new GuiButton(7, cacheWidth + 4, height - 28, 72, 20, Lang.format("selectWorld.recreate")));
		buttonList.add(new GuiButton(0, cacheWidth + 82, height - 28, 72, 20, Lang.format("gui.cancel")));
		selectButton.enabled = false;
		deleteButton.enabled = false;
		renameButton.enabled = false;
		recreateButton.enabled = false;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button.id == 2) {
				String saveName = getSaveName(selectedWorld);

				if (saveName != null) {
					deleteWorld = true;
					mc.displayGuiScreen(guiDeleteWorld(this, saveName, selectedWorld));
				}
			} else if (button.id == 1) loadWorld(selectedWorld);
			else if (button.id == 3) mc.displayGuiScreen(new GuiCreateWorld(this));
			else if (button.id == 6) mc.displayGuiScreen(new GuiRenameWorld(this, getFileName(selectedWorld)));
			else if (button.id == 0) mc.displayGuiScreen(parentScreen);
			else if (button.id == 7) {
				GuiCreateWorld guicreateworld = new GuiCreateWorld(this);
				ISaveHandler isavehandler = mc.worldController.getSaveLoader().getSaveLoader(getFileName(selectedWorld), false);
				WorldInfo worldinfo = isavehandler.loadWorldInfo();
				isavehandler.flush();
				guicreateworld.func_146318_a(worldinfo);
				mc.displayGuiScreen(guicreateworld);
			} else worlds.actionPerformed(button);
		}
	}

	private void loadWorld(int selectedWorld) {
		mc.displayGuiScreen(null);

		if (!lock) {
			lock = true;
			String fileName = getFileName(selectedWorld);

			if (fileName == null) fileName = "World" + selectedWorld;

			String saveName = getSaveName(selectedWorld);

			if (saveName == null) saveName = "World" + selectedWorld;

			if (mc.worldController.getSaveLoader().canLoadWorld(fileName))
				mc.worldController.launchIntegratedServer(fileName, saveName, null, mc);
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if (deleteWorld) {
			deleteWorld = false;

			if (result) {
				ISaveFormat isaveformat = mc.worldController.getSaveLoader();
				isaveformat.flushCache();
				isaveformat.deleteWorldDirectory(getFileName(id));

				try {
					getSaveList();
				} catch (AnvilConverterException anvilconverterexception) {
					logger.error("Couldn\'t load level list", anvilconverterexception);
				}
			}

			mc.displayGuiScreen(this);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		worlds.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(fontRendererObj, select, width >> 1, 20, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public static GuiYesNo guiDeleteWorld(GuiYesNoCallback callback, String saveName, int pressedButton) {
		String s = Lang.format("selectWorld.deleteQuestion");
		String s1 = "\'" + saveName + "\' " + Lang.format("selectWorld.deleteWarning");
		String s2 = Lang.format("selectWorld.deleteButton");
		String s3 = Lang.format("gui.cancel");
		return new GuiYesNo(callback, s, s1, s2, s3, pressedButton);
	}

	class List extends GuiSlot {

		public List(Minecraft mcIn) {
			super(mcIn, GuiSelectWorld.this.width, GuiSelectWorld.this.height, 32,
					GuiSelectWorld.this.height - 64, 36);
		}

		@Override
		protected int getSize() {
			return GuiSelectWorld.this.saveList.size();
		}

		@Override
		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			GuiSelectWorld.this.selectedWorld = slotIndex;
			boolean flag = GuiSelectWorld.this.selectedWorld >= 0 && GuiSelectWorld.this.selectedWorld < this.getSize();
			GuiSelectWorld.this.selectButton.enabled = flag;
			GuiSelectWorld.this.deleteButton.enabled = flag;
			GuiSelectWorld.this.renameButton.enabled = flag;
			GuiSelectWorld.this.recreateButton.enabled = flag;

			if (isDoubleClick && flag) GuiSelectWorld.this.loadWorld(slotIndex);
		}

		@Override
		protected boolean isSelected(int slotIndex) {
			return slotIndex == GuiSelectWorld.this.selectedWorld;
		}

		@Override
		protected int getContentHeight() {
			return GuiSelectWorld.this.saveList.size() * 36;
		}

		@Override
		protected void drawBackground() {
			GuiSelectWorld.this.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int unused, int mouseXIn, int mouseYIn) {
			SaveFormatComparator saveformatcomparator = GuiSelectWorld.this.saveList.get(entryID);
			String name = saveformatcomparator.getDisplayName();

			if (StringUtils.isEmpty(name)) name = GuiSelectWorld.this.selectWorld + " " + (entryID + 1);

			String fileName = saveformatcomparator.getFileName();
			fileName += " (" + GuiSelectWorld.this.dateFormat.format(new Date(saveformatcomparator.getLastTimePlayed()));
			fileName += ")";
			String s2 = "";

			if (saveformatcomparator.requiresConversion()) s2 = GuiSelectWorld.this.conversionWorld + " " + s2;
			else {
				s2 = GuiSelectWorld.this.gamemodes[saveformatcomparator.getEnumGameType().getID()];

				if (saveformatcomparator.isHardcoreModeEnabled())
					s2 = EnumChatFormatting.DARK_RED + Lang.format(
							"gameMode.hardcore", new Object[0]) + EnumChatFormatting.RESET;

				if (saveformatcomparator.getCheatsEnabled())
					s2 = s2 + ", " + Lang.format("selectWorld.cheats");
			}

			GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, name, p_180791_2_ + 2, p_180791_3_ + 1, 16777215);
			GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, fileName, p_180791_2_ + 2, p_180791_3_ + 12, 8421504);
			GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s2, p_180791_2_ + 2, p_180791_3_ + 12 + 10, 8421504);
		}

	}

}
