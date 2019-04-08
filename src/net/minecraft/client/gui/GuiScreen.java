package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.Logger;
import net.minecraft.Utils;
import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.gui.element.GuiLabel;
import net.minecraft.client.gui.element.VolumeSlider;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback {

	private static final Logger LOGGER = Logger.getInstance();
	private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
	private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');

	/**
	 * Reference to the Minecraft object.
	 */
	protected Minecraft mc;

	/**
	 * Holds a instance of RenderItem, used to draw the achievement icons on screen (is based on ItemStack)
	 */
	public RenderItem itemRender;

	/**
	 * The width of the screen object.
	 */
	public int width;

	/**
	 * The height of the screen object.
	 */
	public int height;
	protected List<GuiButton> buttonList = Lists.newArrayList();
	protected List<GuiLabel> labelList = Lists.newArrayList();
	public boolean allowUserInput;

	/**
	 * The FontRenderer used by GuiScreen
	 */
	protected AssetsFontRenderer fontRendererObj;

	/**
	 * The button that was just pressed.
	 */
	private GuiButton selectedButton;
	private int eventButton;
	private long lastMouseEvent;

	/**
	 * Incremented when the game is in touchscreen mode and the screen is tapped, decremented if the screen isn't
	 * tapped. Does not appear to be used.
	 */
	private int touchValue;
	private URI clickedLinkURI;
	private boolean supportButtonOverlap = false;

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		HoverButton hovered = null;
		for (GuiButton button : this.buttonList) {
			button.drawButton(this.mc, mouseX, mouseY);
			if (button instanceof HoverButton) {
				if (button.visible &&
						mouseX >= button.xPosition &&
						mouseX <= button.xPosition + button.width &&
						mouseY >= button.yPosition &&
						mouseY <= button.yPosition + button.height ||
						button instanceof VolumeSlider && ((VolumeSlider) button).superPressed(mc, mouseX, mouseY)
				) hovered = (HoverButton) button;
			}
		}
		if (hovered != null) drawHoveringText(hovered.getHoverText(), mouseX, mouseY);

		for (GuiLabel aLabelList : this.labelList) {
			aLabelList.drawLabel(this.mc, mouseX, mouseY);
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			this.mc.displayGuiScreen(null);

			if (this.mc.currentScreen == null) {
				this.mc.setIngameFocus();
			}
		}
	}

	protected void renderToolTip(ItemStack stack, int x, int y) {
		List<String> list = stack.getTooltip(MC.getPlayer(), false);
		String advanced = stack.getAdvancedToolTip();
		int width = 0;
		for (int i = 0; i < list.size(); ++i) {
			int w = MC.getFontRenderer().getStringWidth(list.get(i));
			if (width < w) width = w;
			if (i == 0) list.set(i, stack.getRarity().rarityColor + list.get(i));
			else list.set(i, EnumChatFormatting.GRAY + list.get(i));
		}
		int advWidth = MC.getFontRenderer().getStringWidth(advanced);
		if (Settings.ITEM_TOOLTIPS.b() && width < advWidth) width = advWidth;
		this.drawHoveringText(list, x, y, width);
		int k = 8;
		if (list.size() > 1) k += 2 + (list.size() - 1) * 10;
		if (Settings.ITEM_TOOLTIPS.b())
			drawHoveringText(Collections.singletonList(advanced), x, y + k + 7, width, 0xd7000000, 0xd7ffa114);
	}


	/**
	 * Draws the text when mouse is over creative inventory tab. Params: current creative tab to be checked, current
	 * mouse x position, current mouse y position.
	 */
	protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
		this.drawHoveringText(Collections.singletonList(tabName), mouseX, mouseY);
	}

	/**
	 * Draws a List of strings as a tooltip. Every entry is drawn on a seperate line.
	 */
	public void drawHoveringText(List<String> textLines, int x, int y, int textWidth, int backgroundColor, int stripColor) {
		if (textLines.isEmpty()) return;
		G.disableDepth();
		G.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		G.disableLighting();
		// Ширина рамки по самой длинной строке
		if (textWidth < 0) for (String s : textLines) {
			int j = this.fontRendererObj.getStringWidth(s);
			if (j > textWidth) textWidth = j;
		}

		int posX = x + 12;
		int posY = y - 12;
		int k = 8;

		if (textLines.size() > 1) k += 2 + (textLines.size() - 1) * 10;
		if (posX + textWidth > this.width) posX -= 28 + textWidth;
		if (posY + k + 6 > this.height) posY = this.height - k - 6;

		this.zLevel = 300.0F;
		this.itemRender.zLevel = 300.0F;

		if (Settings.FANCY_BUTTONS.b()) {
			drawRect(posX - 3, posY - 4, posX + textWidth + 3, posY + k + 3, backgroundColor);
			drawRect(posX - 5, posY - 4, posX - 3, posY + k + 3, stripColor);
		} else {
			int l = -267386864;
			this.drawGradientRect(posX - 3, posY - 4, posX + textWidth + 3, posY - 3, l, l);
			this.drawGradientRect(posX - 3, posY + k + 3, posX + textWidth + 3, posY + k + 4, l, l);
			this.drawGradientRect(posX - 3, posY - 3, posX + textWidth + 3, posY + k + 3, l, l);
			this.drawGradientRect(posX - 4, posY - 3, posX - 3, posY + k + 3, l, l);
			this.drawGradientRect(posX + textWidth + 3, posY - 3, posX + textWidth + 4, posY + k + 3, l, l);
			int i1 = 1347420415;
			int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
			this.drawGradientRect(posX - 3, posY - 3 + 1, posX - 3 + 1, posY + k + 3 - 1, i1, j1);
			this.drawGradientRect(posX + textWidth + 2, posY - 3 + 1, posX + textWidth + 3, posY + k + 3 - 1, i1, j1);
			this.drawGradientRect(posX - 3, posY - 3, posX + textWidth + 3, posY - 3 + 1, i1, i1);
			this.drawGradientRect(posX - 3, posY + k + 2, posX + textWidth + 3, posY + k + 3, j1, j1);
		}

		for (int k1 = 0; k1 < textLines.size(); ++k1) {
			String s1 = textLines.get(k1);
			this.fontRendererObj.drawStringWithShadow(s1, (float) posX, (float) posY, -1);
			if (k1 == 0) posY += 2;
			posY += 10;
		}

		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
//		GlStateManager.enableLighting();
//		RenderHelper.enableStandardItemLighting();
//		GlStateManager.enableRescaleNormal();
		G.enableDepth();
	}

	protected void drawHoveringText(List<String> textLines, int x, int y) {
		drawHoveringText(textLines, x, y, -1);
	}

	protected void drawHoveringText(List<String> textLines, int x, int y, int width) {
//		drawHoveringText(textLines, x, y, width, 0xd70f3842, 0xff217b91);
		drawHoveringText(textLines, x, y, width, 0xe7252525, 0xd7ffa114);
	}

	/**
	 * Draws the hover event specified by the given chat component
	 */
	protected void handleComponentHover(IChatComponent component, int x, int y) {
		if (component == null || component.getChatStyle().getChatHoverEvent() == null) return;
		HoverEvent hoverevent = component.getChatStyle().getChatHoverEvent();
		switch (hoverevent.getAction()) {
			case SHOW_ITEM:
				ItemStack itemstack = null;
				try {
					NBTTagCompound nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());
					itemstack = ItemStack.loadItemStackFromNBT(nbtbase);
				} catch (NBTException ignored) {}

				if (itemstack != null) this.renderToolTip(itemstack, x, y);
				else this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
				break;
			case SHOW_ENTITY:
				if (Settings.ITEM_TOOLTIPS.b()) {
					try {
						NBTTagCompound nbtbase1 = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

						if (nbtbase1 instanceof NBTTagCompound) {
							List<String> list1 = Lists.newArrayList();
							list1.add(nbtbase1.getString("name"));

							if (nbtbase1.hasKey("type", 8)) {
								String s = nbtbase1.getString("type");
								list1.add("Type: " + s + " (" + EntityList.getIDFromString(s) + ")");
							}

							list1.add(nbtbase1.getString("id"));
							this.drawHoveringText(list1, x, y);
						} else this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
					} catch (NBTException var10) {
						this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
					}
				}
				break;
			case SHOW_FILE:
				drawHoveringText(Lists.newArrayList(isCtrlKeyDown() ? "Показать файл в проводнике" : "Открыть файл",
						"§e" + hoverevent.getValue().getFormattedText(), "§f", "§7§oЗажмите Ctrl, чтобы", "§7§oоткрыть папку файла."), x, y);
				break;
			case SHOW_TEXT:
				this.drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverevent.getValue().getFormattedText()), x, y);
				break;
			case SHOW_ACHIEVEMENT:
				StatBase statbase = StatList.getOneShotStat(hoverevent.getValue().getUnformattedText());

				if (statbase != null) {
					IChatComponent ichatcomponent = statbase.getStatName();
					IChatComponent ichatcomponent1 = new ChatComponentTranslation("stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"));
					ichatcomponent1.getChatStyle().setItalic(Boolean.TRUE);
					String s1 = statbase instanceof Achievement ? ((Achievement) statbase).getDescription() : null;
					List<String> list = Lists.newArrayList(ichatcomponent.getFormattedText(), ichatcomponent1.getFormattedText());

					if (s1 != null) {
						list.addAll(this.fontRendererObj.listFormattedStringToWidth(s1, 150));
					}

					this.drawHoveringText(list, x, y);
				} else {
					this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
				}
				break;
		}

		G.disableLighting();
	}

	/**
	 * Sets the text of the chat
	 */
	protected void setText(String newChatText, boolean shouldOverwrite) {
	}

	/**
	 * Executes the click event specified by the given chat component
	 */
	protected boolean handleComponentClick(IChatComponent p_175276_1_) {
		if (p_175276_1_ == null) {
			return false;
		}
		ClickEvent clickevent = p_175276_1_.getChatStyle().getChatClickEvent();

		if (isShiftKeyDown()) {
			if (p_175276_1_.getChatStyle().getInsertion() != null) {
				this.setText(p_175276_1_.getChatStyle().getInsertion(), false);
			}
		} else if (clickevent != null) {
			if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
				if (!Settings.CHAT_LINKS.b())
					return false;


				try {
					URI uri = new URI(clickevent.getValue());
					String s = uri.getScheme();

					if (s == null) {
						throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
					}

					if (!PROTOCOLS.contains(s.toLowerCase())) {
						throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase());
					}

					if (Settings.CHAT_LINKS_PROMPT.b()) {
						this.clickedLinkURI = uri;
						this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickevent.getValue(), 31102009, false));
					} else this.openWebLink(uri);
				} catch (URISyntaxException urisyntaxexception) {
					LOGGER.error("Can\'t open url for " + clickevent, urisyntaxexception);
				}
			} else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
				File f = new File(clickevent.getValue());
				System.out.println(f.getName());
				if (isCtrlKeyDown()) showFile(f);
				else openFile(f);
//				URI uri1 = new File(clickevent.getValue()).toURI();
//				this.openWebLink(uri1);
			} else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
				this.setText(clickevent.getValue(), true);
			} else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.sendChatMessage(clickevent.getValue(), false);
			} else {
				LOGGER.error("Don\'t know how to handle " + clickevent);
			}

			return true;
		}

		return false;
	}

	public void showFile(File f) {
		if (Util.getOSType() != Util.OS.WINDOWS) openFile(f.getParentFile());
		else {
			try {
				Runtime.getRuntime().exec("explorer.exe /select," + f.getAbsolutePath());
			} catch (IOException e) {
				MC.i().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§cПри открытии скриншота произошла ошибка."));
			}
		}
	}

	public void sendChatMessage(String msg) {
		this.sendChatMessage(msg, true);
	}

	public void sendChatMessage(String msg, boolean addToChat) {
		if (addToChat) {
			this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
		}

		this.mc.thePlayer.sendChatMessage(msg);
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (mouseButton == 0) {
			for (GuiButton guibutton : this.buttonList) {
				if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
					this.selectedButton = guibutton;
					guibutton.playPressSound(this.mc.getSoundHandler());
					this.actionPerformed(guibutton);
					if (!supportButtonOverlap) break;
				}
			}
		}
	}

	/**
	 * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
	 */
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (this.selectedButton != null && state == 0) {
			this.selectedButton.mouseReleased(mouseX, mouseY);
			this.selectedButton = null;
		}
	}

	/**
	 * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
	 * lastButtonClicked & timeSinceMouseClick.
	 */
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
	}

	/**
	 * Causes the screen to lay out its subcomponents again. This is the equivalent of the Java call
	 * Container.validate()
	 */
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		this.mc = mc;
		this.itemRender = mc.getRenderItem();
		this.fontRendererObj = mc.fontRendererObj;
		this.width = width;
		this.height = height;
		this.buttonList.clear();
		this.initGui();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public abstract void initGui();

	/**
	 * Delegates mouse and keyboard input.
	 */
	public void handleInput() throws IOException {
		if (Mouse.isCreated()) {
			while (Mouse.next()) {
				this.handleMouseInput();
			}
		}

		if (Keyboard.isCreated()) {
			while (Keyboard.next()) {
				this.handleKeyboardInput();
			}
		}
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() throws IOException {
		int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int k = Mouse.getEventButton();

		if (Mouse.getEventButtonState()) {
			this.eventButton = k;
			this.lastMouseEvent = Minecraft.getSystemTime();
			this.mouseClicked(i, j, this.eventButton);
		} else if (k != -1) {
			this.eventButton = -1;
			this.mouseReleased(i, j, k);
		} else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
			long l = Minecraft.getSystemTime() - this.lastMouseEvent;
			this.mouseClickMove(i, j, this.eventButton, l);
		}
	}

	/**
	 * Handles keyboard input.
	 */
	public void handleKeyboardInput() throws IOException {
		if (Keyboard.getEventKeyState()) {
			this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
		}

		this.mc.dispatchKeypresses();
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
	}

	/**
	 * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
	 */
	public void drawDefaultBackground() {
		this.drawWorldBackground(0);
	}

	public void drawWorldBackground(int tint) {
		if (this.mc.theWorld != null) this.drawGradientRect(0, 0, this.width, this.height, 0xc0101010, 0xd0101010);
		else this.drawBackground(tint);
	}

	/**
	 * Draws the background (i is always 0 as of 1.2.2)
	 */
	public void drawBackground(int tint) {
		G.disableLighting();
		G.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		this.mc.getTextureManager().bindTexture(optionsBackground);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(0.0D, (double) this.height, 0.0D).tex(0.0D, (double) ((float) this.height / 32.0F + (float) tint)).color(64, 64, 64, 255).endVertex();
		worldrenderer.pos((double) this.width, (double) this.height, 0.0D).tex((double) ((float) this.width / 32.0F), (double) ((float) this.height / 32.0F + (float) tint)).color(64, 64, 64,
				255).endVertex();
		worldrenderer.pos((double) this.width, 0.0D, 0.0D).tex((double) ((float) this.width / 32.0F), (double) tint).color(64, 64, 64, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double) tint).color(64, 64, 64, 255).endVertex();
		tessellator.draw();
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	public boolean doesGuiPauseGame() {
		return true;
	}

	public void confirmClicked(boolean result, int id) {
		if (id == 31102009) {
			if (result) {
				this.openWebLink(this.clickedLinkURI);
			}

			this.clickedLinkURI = null;
			this.mc.displayGuiScreen(this);
		}
	}

	private void openFile(File f) {
		try {
			Class<?> oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", Utils.CLASS).invoke(null);
			oclass.getMethod("open", File.class).invoke(object, f);
		} catch (Throwable throwable) {
			LOGGER.error("Couldn\'t open link", throwable);
		}
	}

	private void openWebLink(URI p_175282_1_) {
		try {
			Class<?> oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", Utils.CLASS).invoke(null);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, p_175282_1_);
		} catch (Throwable throwable) {
			LOGGER.error("Couldn\'t open link", throwable);
		}
	}

	/**
	 * Returns true if either windows ctrl key is down or if either mac meta key is down
	 */
	public static boolean isCtrlKeyDown() {
		return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
	}

	/**
	 * Returns true if either shift key is down
	 */
	public static boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
	}

	/**
	 * Returns true if either alt key is down
	 */
	public static boolean isAltKeyDown() {
		return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
	}

	public static boolean isKeyComboCtrlX(int p_175277_0_) {
		return p_175277_0_ == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
	}

	public static boolean isKeyComboCtrlV(int p_175279_0_) {
		return p_175279_0_ == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
	}

	public static boolean isKeyComboCtrlC(int p_175280_0_) {
		return p_175280_0_ == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
	}

	public static boolean isKeyComboCtrlA(int p_175278_0_) {
		return p_175278_0_ == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
	}

	/**
	 * Called when the GUI is resized in order to update the world and the resolution
	 */
	public void onResize(Minecraft mcIn, int p_175273_2_, int p_175273_3_) {
		this.setWorldAndResolution(mcIn, p_175273_2_, p_175273_3_);
	}

}
