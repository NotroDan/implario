package net.minecraft.client.gui;

import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.gui.element.GuiTextField;
import net.minecraft.client.resources.Lang;
import net.minecraft.util.chat.ChatAllowedCharacters;
import net.minecraft.world.WorldCustomizer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Random;

public class GuiCreateWorld extends GuiScreen {

	private GuiScreen parentScreen;
	private GuiTextField txtName;
	private GuiTextField txtSeed;
	private String field_146336_i;
	private String gameMode = "survival";
	private String field_175300_s;
	private boolean enableMapFeatures = true;

	/**
	 * If cheats are allowed
	 */
	private boolean allowCheats;
	private boolean touchedAllowCheatsButton;
	private boolean starterKit;
	private boolean hardcore;
	private boolean alreadyCreating;
	private boolean showMoreOptions;
	private GuiButton btnGameMode;
	private GuiButton btnMoreOptions;
	private GuiButton btnMapFeatures;
	private GuiButton btnBonusItems;
	private GuiButton btnMapType;
	private GuiButton btnAllowCommands;
	private GuiButton btnCustomizeType;
	private String field_146323_G;
	private String field_146328_H;
	private String seed;
	private String field_146330_J;
	private int selectedIndex;
	public String chunkProviderSettingsJson = "";

	/**
	 * These filenames are known to be restricted on one or more OS's.
	 */
	private static final String[] disallowedFilenames = new String[] {
			"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
	};

	public GuiCreateWorld(GuiScreen p_i46320_1_) {
		this.parentScreen = p_i46320_1_;
		this.seed = "";
		this.field_146330_J = Lang.format("selectWorld.newWorld");
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		this.txtName.updateCursorCounter();
		this.txtSeed.updateCursorCounter();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, Lang.format("selectWorld.create")));
		this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, Lang.format("gui.cancel")));
		this.buttonList.add(this.btnGameMode = new GuiButton(2, this.width / 2 - 75, 115, 150, 20, Lang.format("selectWorld.gameMode")));
		this.buttonList.add(this.btnMoreOptions = new GuiButton(3, this.width / 2 - 75, 187, 150, 20, Lang.format("selectWorld.moreWorldOptions")));
		this.buttonList.add(this.btnMapFeatures = new GuiButton(4, this.width / 2 - 155, 100, 150, 20, Lang.format("selectWorld.mapFeatures")));
		this.btnMapFeatures.visible = false;
		this.buttonList.add(this.btnBonusItems = new GuiButton(7, this.width / 2 + 5, 151, 150, 20, Lang.format("selectWorld.bonusItems")));
		this.btnBonusItems.visible = false;
		this.buttonList.add(this.btnMapType = new GuiButton(5, this.width / 2 + 5, 100, 150, 20, Lang.format("selectWorld.mapType")));
		this.btnMapType.visible = false;
		this.buttonList.add(this.btnAllowCommands = new GuiButton(6, this.width / 2 - 155, 151, 150, 20, Lang.format("selectWorld.allowCommands")));
		this.btnAllowCommands.visible = false;
		this.buttonList.add(this.btnCustomizeType = new GuiButton(8, this.width / 2 + 5, 120, 150, 20, Lang.format("selectWorld.customizeType")));
		this.btnCustomizeType.visible = false;
		this.txtName = new GuiTextField(9, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
		this.txtName.setFocused(true);
		this.txtName.setText(this.field_146330_J);
		this.txtSeed = new GuiTextField(10, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
		this.txtSeed.setText(this.seed);
		this.updateLayout(this.showMoreOptions);
		this.func_146314_g();
		this.updateCaptions();
	}

	private void func_146314_g() {
		this.field_146336_i = this.txtName.getText().trim();

		for (char c0 : ChatAllowedCharacters.disallowedChars) {
			this.field_146336_i = this.field_146336_i.replace(c0, '_');
		}

		if (StringUtils.isEmpty(this.field_146336_i)) {
			this.field_146336_i = "World";
		}

		this.field_146336_i = func_146317_a(this.mc.worldController.getSaveLoader(), this.field_146336_i);
	}

	private void updateCaptions() {
		this.btnGameMode.displayString = Lang.format("selectWorld.gameMode") + ": " + Lang.format("selectWorld.gameMode." + this.gameMode);
		this.field_146323_G = Lang.format("selectWorld.gameMode." + this.gameMode + ".line1");
		this.field_146328_H = Lang.format("selectWorld.gameMode." + this.gameMode + ".line2");
		this.btnMapFeatures.displayString = Lang.format("selectWorld.mapFeatures") + " ";

		if (this.enableMapFeatures) {
			this.btnMapFeatures.displayString = this.btnMapFeatures.displayString + Lang.format("options.on");
		} else {
			this.btnMapFeatures.displayString = this.btnMapFeatures.displayString + Lang.format("options.off");
		}

		this.btnBonusItems.displayString = Lang.format("selectWorld.bonusItems") + " ";

		if (this.starterKit && !this.hardcore) {
			this.btnBonusItems.displayString = this.btnBonusItems.displayString + Lang.format("options.on");
		} else {
			this.btnBonusItems.displayString = this.btnBonusItems.displayString + Lang.format("options.off");
		}

		this.btnMapType.displayString = Lang.format("selectWorld.mapType") + " " + Lang.format(WorldType.worldTypes[this.selectedIndex].getTranslateName());
		this.btnAllowCommands.displayString = Lang.format("selectWorld.allowCommands") + " ";

		if (this.allowCheats && !this.hardcore) {
			this.btnAllowCommands.displayString = this.btnAllowCommands.displayString + Lang.format("options.on");
		} else {
			this.btnAllowCommands.displayString = this.btnAllowCommands.displayString + Lang.format("options.off");
		}
	}

	public static String func_146317_a(ISaveFormat saveFormat, String name) {

		StringBuilder b = new StringBuilder(name.replaceAll("[\\./\"]", "_"));
		for (String s : disallowedFilenames) {
			if (b.toString().equalsIgnoreCase(s)) {
				b = new StringBuilder("_" + b + "_");
			}
		}
		name = b.toString();

		while (saveFormat.getWorldInfo(name) != null) {
			name = name + "-";
		}

		return name;
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (!button.enabled) return;

		// Выход в главное меню
		if (button.id == 1) {
			this.mc.displayGuiScreen(this.parentScreen);
			return;
		}

		// Создать мир
		if (button.id == 0) {
			this.mc.displayGuiScreen(null);

			if (this.alreadyCreating) return;
			this.alreadyCreating = true;

			long seed = new Random().nextLong();
			String s = this.txtSeed.getText();

			if (!StringUtils.isEmpty(s)) {
				try {
					long j = Long.parseLong(s);
					if (j != 0L) seed = j;
				} catch (NumberFormatException var7) {
					seed = (long) s.hashCode();
				}
			}

			WorldSettings.GameType gameType = WorldSettings.GameType.getByName(this.gameMode);
			WorldSettings worldsettings = new WorldSettings(seed, gameType, this.enableMapFeatures, this.hardcore, WorldType.worldTypes[this.selectedIndex]);
			worldsettings.setWorldName(this.chunkProviderSettingsJson);

			if (this.starterKit && !this.hardcore) worldsettings.enableStarterKit();
			if (this.allowCheats && !this.hardcore) worldsettings.enableCommands();

			this.mc.worldController.launchIntegratedServer(this.field_146336_i, this.txtName.getText().trim(), worldsettings, this.mc);
			return;
		}

		// Открыть/закрыть дополнительные настройки
		if (button.id == 3) {
			this.toggleLayout();
			return;
		}

		// Режим игры
		if (button.id == 2) {
			switch (this.gameMode) {
				case "survival":
					if (!this.touchedAllowCheatsButton) {
						this.allowCheats = false;
					}

					this.hardcore = false;
					this.gameMode = "hardcore";
					this.hardcore = true;
					this.btnAllowCommands.enabled = false;
					this.btnBonusItems.enabled = false;
					this.updateCaptions();
					break;
				case "hardcore":
					if (!this.touchedAllowCheatsButton) {
						this.allowCheats = true;
					}

					this.hardcore = false;
					this.gameMode = "creative";
					this.updateCaptions();
					this.hardcore = false;
					this.btnAllowCommands.enabled = true;
					this.btnBonusItems.enabled = true;
					break;
				default:
					if (!this.touchedAllowCheatsButton) this.allowCheats = false;

					this.gameMode = "survival";
					this.updateCaptions();
					this.btnAllowCommands.enabled = true;
					this.btnBonusItems.enabled = true;
					this.hardcore = false;
					break;
			}

			this.updateCaptions();
			return;
		}

		if (button.id == 4) {
			this.enableMapFeatures = !this.enableMapFeatures;
			this.updateCaptions();
		} else if (button.id == 7) {
			this.starterKit = !this.starterKit;
			this.updateCaptions();
		} else if (button.id == 5) {
			++this.selectedIndex;

			if (this.selectedIndex >= WorldType.worldTypes.length) {
				this.selectedIndex = 0;
			}

			while (!this.isSelectedTypeAllowed()) {
				++this.selectedIndex;

				if (this.selectedIndex >= WorldType.worldTypes.length) {
					this.selectedIndex = 0;
				}
			}

			this.chunkProviderSettingsJson = "";
			this.updateCaptions();
			this.updateLayout(this.showMoreOptions);
		} else if (button.id == 6) {
			this.touchedAllowCheatsButton = true;
			this.allowCheats = !this.allowCheats;
			this.updateCaptions();
		} else if (button.id == 8) {
			WorldCustomizer c = WorldType.worldTypes[this.selectedIndex].getCustomizer();
			if (c != null) c.openCustomizationGui(this, this.chunkProviderSettingsJson);
		}
	}

	private boolean isSelectedTypeAllowed() {
		WorldType worldtype = WorldType.worldTypes[this.selectedIndex];
		return worldtype != null && worldtype.getCanBeCreated() && (worldtype != WorldType.DEBUG || isShiftKeyDown());
	}

	private void toggleLayout() {
		this.updateLayout(!this.showMoreOptions);
	}

	private void updateLayout(boolean showMoreOptions) {
		this.showMoreOptions = showMoreOptions;
		WorldType type = WorldType.worldTypes[this.selectedIndex];
		if (type == WorldType.DEBUG) {
			this.btnGameMode.visible = !this.showMoreOptions;
			this.btnGameMode.enabled = false;

			if (this.field_175300_s == null) {
				this.field_175300_s = this.gameMode;
			}

			this.gameMode = "spectator";
			this.btnMapFeatures.visible = false;
			this.btnBonusItems.visible = false;
			this.btnMapType.visible = this.showMoreOptions;
			this.btnAllowCommands.visible = false;
			this.btnCustomizeType.visible = false;
		} else {
			this.btnGameMode.visible = !this.showMoreOptions;
			this.btnGameMode.enabled = true;

			if (this.field_175300_s != null) {
				this.gameMode = this.field_175300_s;
				this.field_175300_s = null;
			}

			this.btnMapFeatures.visible = this.showMoreOptions && type.areMapFeaturesEnabled();
			this.btnBonusItems.visible = this.showMoreOptions;
			this.btnMapType.visible = this.showMoreOptions;
			this.btnAllowCommands.visible = this.showMoreOptions;
			this.btnCustomizeType.visible = this.showMoreOptions && type.getCustomizer() != null;
		}

		this.updateCaptions();

		if (this.showMoreOptions) {
			this.btnMoreOptions.displayString = Lang.format("gui.done");
		} else {
			this.btnMoreOptions.displayString = Lang.format("selectWorld.moreWorldOptions");
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.txtName.isFocused() && !this.showMoreOptions) {
			this.txtName.textboxKeyTyped(typedChar, keyCode);
			this.field_146330_J = this.txtName.getText();
		} else if (this.txtSeed.isFocused() && this.showMoreOptions) {
			this.txtSeed.textboxKeyTyped(typedChar, keyCode);
			this.seed = this.txtSeed.getText();
		}

		if (keyCode == 28 || keyCode == 156) {
			this.actionPerformed(this.buttonList.get(0));
		}

		this.buttonList.get(0).enabled = this.txtName.getText().length() > 0;
		this.func_146314_g();
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (this.showMoreOptions) {
			this.txtSeed.mouseClicked(mouseX, mouseY, mouseButton);
		} else {
			this.txtName.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, Lang.format("selectWorld.create"), this.width / 2, 20, -1);

		if (this.showMoreOptions) {
			this.drawString(this.fontRendererObj, Lang.format("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.fontRendererObj, Lang.format("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);

			if (this.btnMapFeatures.visible) {
				this.drawString(this.fontRendererObj, Lang.format("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
			}

			if (this.btnAllowCommands.visible) {
				this.drawString(this.fontRendererObj, Lang.format("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
			}

			this.txtSeed.drawTextBox();

			if (WorldType.worldTypes[this.selectedIndex].showWorldInfoNotice()) {
				this.fontRendererObj.drawSplitString(Lang.format(WorldType.worldTypes[this.selectedIndex].func_151359_c()), this.btnMapType.xPosition + 2,
						this.btnMapType.yPosition + 22, this.btnMapType.getButtonWidth(), 10526880);
			}
		} else {
			this.drawString(this.fontRendererObj, Lang.format("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
			this.drawString(this.fontRendererObj, Lang.format("selectWorld.resultFolder") + " " + this.field_146336_i, this.width / 2 - 100, 85, -6250336);
			this.txtName.drawTextBox();
			this.drawString(this.fontRendererObj, this.field_146323_G, this.width / 2 - 100, 137, -6250336);
			this.drawString(this.fontRendererObj, this.field_146328_H, this.width / 2 - 100, 149, -6250336);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void func_146318_a(WorldInfo info) {
		this.field_146330_J = Lang.format("selectWorld.newWorld.copyOf", info.getWorldName());
		this.seed = info.getSeed() + "";
		this.selectedIndex = info.getTerrainType().getWorldTypeID();
		this.chunkProviderSettingsJson = info.getGeneratorOptions();
		this.enableMapFeatures = info.isMapFeaturesEnabled();
		this.allowCheats = info.areCommandsAllowed();

		if (info.isHardcoreModeEnabled()) {
			this.gameMode = "hardcore";
		} else if (info.getGameType().isSurvivalOrAdventure()) {
			this.gameMode = "survival";
		} else if (info.getGameType().isCreative()) {
			this.gameMode = "creative";
		}
	}

}
