package net.minecraft.client.gui.inventory;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.element.Colors;
import net.minecraft.client.gui.element.RenderRec;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Set;

import static net.minecraft.inventory.ClickType.*;

public abstract class GuiContainer extends GuiScreen {

	/**
	 * The location of the inventory background texture
	 */
	protected static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");

	/**
	 * The X size of the inventory window in pixels.
	 */
	protected int xSize = 176;

	/**
	 * The Y size of the inventory window in pixels.
	 */
	protected int ySize = 166;

	/**
	 * A list of the players inventory slots
	 */
	public Container inventorySlots;

	/**
	 * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
	 */
	protected int guiLeft;

	/**
	 * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
	 */
	protected int guiTop;

	/**
	 * holds the slot currently hovered
	 */
	private Slot theSlot;

	/**
	 * Used when touchscreen is enabled.
	 */
	private Slot clickedSlot;

	/**
	 * Used when touchscreen is enabled.
	 */
	private boolean isRightMouseClick;

	/**
	 * Used when touchscreen is enabled
	 */
	private ItemStack draggedStack;
	private int touchUpX;
	private int touchUpY;
	private Slot returningStackDestSlot;
	private long returningStackTime;

	/**
	 * Used when touchscreen is enabled
	 */
	private ItemStack returningStack;
	private Slot currentDragTargetSlot;
	private long dragItemDropDelay;
	protected final Set<Slot> dragSplittingSlots = Sets.newHashSet();
	protected boolean dragSplitting;
	private int dragSplittingLimit;
	private int dragSplittingButton;
	private boolean ignoreMouseUp;
	private int dragSplittingRemnant;
	private long lastClickTime;
	private Slot lastClickSlot;
	private int lastClickButton;
	private boolean doubleClick;
	private ItemStack shiftClickedSlot;

	public GuiContainer(Container inventorySlotsIn) {
		this.inventorySlots = inventorySlotsIn;
		this.ignoreMouseUp = true;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		int i = this.guiLeft;
		int j = this.guiTop;
		this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		G.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		G.disableLighting();
		G.disableDepth();
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		G.pushMatrix();
		G.translate((float) i, (float) j, 0.0F);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		G.enableRescaleNormal();
		this.theSlot = null;
		int k = 240;
		int l = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k / 1.0F, (float) l / 1.0F);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);

		for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
			Slot slot = this.inventorySlots.inventorySlots.get(i1);
			this.drawSlot(slot);

			if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered()) {
				this.theSlot = slot;
				G.disableLighting();
				G.disableDepth();
				int j1 = slot.xDisplayPosition;
				int k1 = slot.yDisplayPosition;
				G.colorMask(true, true, true, false);
				this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, 0x80ffffff, 0x80ffffff);
				G.colorMask(true, true, true, true);
				G.enableLighting();
				G.enableDepth();
			}
		}

		RenderHelper.disableStandardItemLighting();
		this.drawGuiContainerForegroundLayer(mouseX, mouseY);
		RenderHelper.enableGUIStandardItemLighting();
		InventoryPlayer inventoryplayer = this.mc.thePlayer.inventory;
		ItemStack itemstack = this.draggedStack == null ? inventoryplayer.getItemStack() : this.draggedStack;

		if (itemstack != null) {
			int j2 = 8;
			int k2 = this.draggedStack == null ? 8 : 16;
			String s = null;

			if (this.draggedStack != null && this.isRightMouseClick) {
				itemstack = itemstack.copy();
				itemstack.stackSize = MathHelper.ceiling_float_int((float) itemstack.stackSize / 2.0F);
			} else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
				itemstack = itemstack.copy();
				itemstack.stackSize = this.dragSplittingRemnant;

				if (itemstack.stackSize == 0) s = "§e0";
			}

			drawItemStack(itemstack, mouseX - i - j2, mouseY - j - k2, s);
		}

		if (this.returningStack != null) {
			float f = (float) (Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;

			if (f >= 1.0F) {
				f = 1.0F;
				this.returningStack = null;
			}

			int l2 = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
			int i3 = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
			int l1 = this.touchUpX + (int) ((float) l2 * f);
			int i2 = this.touchUpY + (int) ((float) i3 * f);
			drawItemStack(this.returningStack, l1, i2, null);
		}

		G.popMatrix();

		if (inventoryplayer.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack()) {
			ItemStack itemstack1 = this.theSlot.getStack();
			this.renderToolTip(itemstack1, mouseX, mouseY);
		}

		G.enableLighting();
		G.enableDepth();
		RenderHelper.enableStandardItemLighting();
	}


	private void drawItemStack(ItemStack stack, int x, int y, String altText) {
		zLevel = 200;
		drawItemStack0(this, stack, x, y, altText);
		zLevel = 0;
	}

	/**
	 * Render an ItemStack. Args : stack, x, y, format
	 */
	public void drawItemStack0(GuiScreen screen, ItemStack stack, int x, int y, String altText) {
		G.translate(0.0F, 0.0F, 32.0F);
		ItemRenderer r = Minecraft.getMinecraft().getItemRenderer();
		screen.itemRender.zLevel = 200.0F;
		screen.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		screen.itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, stack, x, y - (this.draggedStack == null ? 0 : 8), altText);
		screen.itemRender.zLevel = 0.0F;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
	}

	/**
	 * Args : renderPartialTicks, mouseX, mouseY
	 */
	protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

	private void drawSlot(Slot slotIn) {
		int i = slotIn.xDisplayPosition;
		int j = slotIn.yDisplayPosition;
		ItemStack itemstack = slotIn.getStack();
		boolean flag = false;
		boolean flag1 = slotIn == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
		ItemStack itemstack1 = this.mc.thePlayer.inventory.getItemStack();
		String s = null;
		if (slotIn == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && itemstack != null) {
			itemstack = itemstack.copy();
			itemstack.stackSize /= 2;
		} else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && itemstack1 != null) {
			if (this.dragSplittingSlots.size() == 1) {
				return;
			}

			if (Container.canAddItemToSlot(slotIn, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slotIn)) {
				itemstack = itemstack1.copy();
				flag = true;
				Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);

				if (itemstack.stackSize > itemstack.getMaxStackSize()) {
					s = EnumChatFormatting.YELLOW + "" + itemstack.getMaxStackSize();
					itemstack.stackSize = itemstack.getMaxStackSize();
				}

				if (itemstack.stackSize > slotIn.getItemStackLimit(itemstack)) {
					s = EnumChatFormatting.YELLOW + "" + slotIn.getItemStackLimit(itemstack);
					itemstack.stackSize = slotIn.getItemStackLimit(itemstack);
				}
			} else {
				this.dragSplittingSlots.remove(slotIn);
				this.updateDragSplitting();
			}
		}

		if (Settings.MODERN_INVENTORIES.b() && Settings.SLOT_GRID.i() != 1) {
			RenderHelper.disableStandardItemLighting();
			RenderRec.render(i, j, 16, 16, Colors.GRAY);
			RenderHelper.enableGUIStandardItemLighting();
		}

		this.zLevel = 100.0F;
		this.itemRender.zLevel = 100.0F;

		if (itemstack == null) {
			String s1 = slotIn.getSlotTexture();

			if (s1 != null) {
				TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite(s1);
				G.disableLighting();
				G.enableAlpha();
				this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
				this.drawTexturedModalRect(i, j, textureatlassprite, 16, 16);
				G.enableLighting();
				flag1 = true;
			}
		}

		if (!flag1) {
			if (flag) drawRect(i, j, i + 16, j + 16, -2130706433);

			G.enableDepth();
			this.itemRender.renderItemAndEffectIntoGUI(itemstack, i, j);
			this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, itemstack, i, j, s);
		}

		this.itemRender.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	private void updateDragSplitting() {
		ItemStack itemstack = this.mc.thePlayer.inventory.getItemStack();

		if (itemstack != null && this.dragSplitting) {
			this.dragSplittingRemnant = itemstack.stackSize;

			for (Slot slot : this.dragSplittingSlots) {
				ItemStack itemstack1 = itemstack.copy();
				int i = slot.getStack() == null ? 0 : slot.getStack().stackSize;
				Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);

				if (itemstack1.stackSize > itemstack1.getMaxStackSize()) {
					itemstack1.stackSize = itemstack1.getMaxStackSize();
				}

				if (itemstack1.stackSize > slot.getItemStackLimit(itemstack1)) {
					itemstack1.stackSize = slot.getItemStackLimit(itemstack1);
				}

				this.dragSplittingRemnant -= itemstack1.stackSize - i;
			}
		}
	}

	/**
	 * Returns the slot at the given coordinates or null if there is none.
	 */
	private Slot getSlotAtPosition(int x, int y) {
		for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
			Slot slot = this.inventorySlots.inventorySlots.get(i);
			if (this.isMouseOverSlot(slot, x, y)) return slot;
		}

		return null;
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		boolean flag = mouseButton == KeyBinding.PICK.getKeyCode() + 100;
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		long i = Minecraft.getSystemTime();
		this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
		this.ignoreMouseUp = false;

		if (mouseButton == 0 || mouseButton == 1 || flag) {
			int j = this.guiLeft;
			int k = this.guiTop;
			boolean flag1 = mouseX < j || mouseY < k || mouseX >= j + this.xSize || mouseY >= k + this.ySize;
			int l = -1;

			if (slot != null) {
				l = slot.slotNumber;
			}

			if (flag1) {
				l = -999;
			}

			if (l != -1) {
				if (!this.dragSplitting) {
					if (this.mc.thePlayer.inventory.getItemStack() == null) {
						if (mouseButton == KeyBinding.PICK.getKeyCode() + 100) {
							this.handleMouseClick(slot, l, mouseButton, PICK);
						} else {
							boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
							ClickType i1 = CLICK;

							if (flag2) {
								this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
								i1 = SHIFT;
							} else if (l == -999) {
								i1 = DROP;
							}

							this.handleMouseClick(slot, l, mouseButton, i1);
						}

						this.ignoreMouseUp = true;
					} else {
						this.dragSplitting = true;
						this.dragSplittingButton = mouseButton;
						this.dragSplittingSlots.clear();

						if (mouseButton == 0) {
							this.dragSplittingLimit = 0;
						} else if (mouseButton == 1) {
							this.dragSplittingLimit = 1;
						} else if (mouseButton == KeyBinding.PICK.getKeyCode() + 100) {
							this.dragSplittingLimit = 2;
						}
					}
				}
			}
		}

		this.lastClickSlot = slot;
		this.lastClickTime = i;
		this.lastClickButton = mouseButton;
	}

	/**
	 * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
	 * lastButtonClicked & timeSinceMouseClick.
	 */
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		ItemStack itemstack = this.mc.thePlayer.inventory.getItemStack();

		if (this.dragSplitting && slot != null && itemstack != null && itemstack.stackSize > this.dragSplittingSlots.size() && Container.canAddItemToSlot(slot, itemstack,
				true) && slot.isItemValid(itemstack) && this.inventorySlots.canDragIntoSlot(slot)) {
			this.dragSplittingSlots.add(slot);
			this.updateDragSplitting();
		}
	}

	/**
	 * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
	 */
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		int i = this.guiLeft;
		int j = this.guiTop;
		boolean flag = mouseX < i || mouseY < j || mouseX >= i + this.xSize || mouseY >= j + this.ySize;
		int k = -1;

		if (slot != null) k = slot.slotNumber;
		if (flag) k = -999;

		if (this.doubleClick && slot != null && state == 0 && this.inventorySlots.canMergeSlot(null, slot)) {
			if (isShiftKeyDown()) {
				if (slot != null && slot.inventory != null && this.shiftClickedSlot != null) {
					for (Slot slot2 : this.inventorySlots.inventorySlots) {
						if (slot2 != null && slot2.canTakeStack(this.mc.thePlayer) && slot2.getHasStack() && slot2.inventory == slot.inventory && Container.canAddItemToSlot(slot2,
								this.shiftClickedSlot, true)) {
							this.handleMouseClick(slot2, slot2.slotNumber, state, SHIFT);
						}
					}
				}
			} else {
				this.handleMouseClick(slot, k, state, COLLECT);
			}

			this.doubleClick = false;
			this.lastClickTime = 0L;
		} else {
			if (this.dragSplitting && this.dragSplittingButton != state) {
				this.dragSplitting = false;
				this.dragSplittingSlots.clear();
				this.ignoreMouseUp = true;
				return;
			}

			if (this.ignoreMouseUp) {
				this.ignoreMouseUp = false;
				return;
			}
			if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
				this.handleMouseClick(null, -999, Container.func_94534_d(0, this.dragSplittingLimit), DRAG);

				for (Slot slot1 : this.dragSplittingSlots) {
					this.handleMouseClick(slot1, slot1.slotNumber, Container.func_94534_d(1, this.dragSplittingLimit), DRAG);
				}

				this.handleMouseClick(null, -999, Container.func_94534_d(2, this.dragSplittingLimit), DRAG);
			} else if (this.mc.thePlayer.inventory.getItemStack() != null) {
				if (state == KeyBinding.PICK.getKeyCode() + 100) this.handleMouseClick(slot, k, state, PICK);
				else {
					boolean flag1 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

					if (flag1) this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;

					this.handleMouseClick(slot, k, state, flag1 ? SHIFT : CLICK);
				}
			}
		}

		if (this.mc.thePlayer.inventory.getItemStack() == null) {
			this.lastClickTime = 0L;
		}

		this.dragSplitting = false;
	}

	/**
	 * Returns if the passed mouse position is over the specified slot. Args : slot, mouseX, mouseY
	 */
	private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
		return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
	}

	/**
	 * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
	 * pointY
	 */
	protected boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
		int i = this.guiLeft;
		int j = this.guiTop;
		pointX = pointX - i;
		pointY = pointY - j;
		return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
	}

	/**
	 * Called when the mouse is clicked over a slot or outside the gui.
	 */
	protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType) {
		if (slotIn != null) {
			slotId = slotIn.slotNumber;
		}

		this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, clickedButton, clickType.ordinal(), this.mc.thePlayer);
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 || keyCode == KeyBinding.INVENTORY.getKeyCode()) this.mc.thePlayer.closeScreen();

		this.checkHotbarKeys(keyCode);

		if (this.theSlot != null && this.theSlot.getHasStack())
			if (keyCode == KeyBinding.PICK.getKeyCode())
				this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, PICK);
			else if (keyCode == KeyBinding.DROP.getKeyCode())
				this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, DROP);
	}

	/**
	 * This function is what controls the hotbar shortcut check when you press a number key when hovering a stack. Args
	 * : keyCode, Returns true if a Hotbar key is pressed, else false
	 */
	protected boolean checkHotbarKeys(int keyCode) {
		if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null)
			for (int i = 0; i < 9; ++i) {
				if (keyCode != KeyBinding.HOTBAR[i].getKeyCode()) continue;
				this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, i, HOTBAR);
				return true;
			}
		return false;
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		if (this.mc.thePlayer != null) {
			this.inventorySlots.onContainerClosed(this.mc.thePlayer);
		}
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		super.updateScreen();

		if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
			this.mc.thePlayer.closeScreen();
		}
	}

	public static void drawModernSlotGrid(int x, int y, int columns, int rows, int color) {
		for (int row = 0; row < rows; row++) {
			int x1 = x;
			for (int col = 0; col < columns; col++) {
				int y1 = y + row * 18;
				drawRect(x1, y1, (x1 += 18) - 2, y1 + 16, color);
			}
		}
	}

}
