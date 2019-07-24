package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.Lang;
import net.minecraft.inventory.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.Groups;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.inventory.ClickType.*;
import static net.minecraft.inventory.ContainerCreativeItems.DUMMY;

public class GuiContainerItems extends InventoryEffectRenderer {

	private float currentScroll;
	private List<Slot> field_147063_B;
	private Slot recycleBin;
	private CreativeCrafting field_147059_E;
	private boolean showInv;

	public GuiContainerItems(EntityPlayer p) {
		super(new ContainerCreativeItems(p));
		guiLeft = width / 2 - ((ContainerCreativeItems) inventorySlots).left;
		p.openContainer = this.inventorySlots;
		this.allowUserInput = true;
		this.ySize = 136;
		this.xSize = (Groups.unoW + Groups.duoW) * 18 + 10;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		if (!this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
		}

		this.updateActivePotionEffects();
	}

	/**
	 * Called when the mouse is clicked over a slot or outside the gui.
	 */
	protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType) {
		boolean flag = clickType == SHIFT;
		clickType = slotId == -999 && clickType == CLICK ? DROP : clickType;

		if (slotIn == null && !showInv && clickType != DRAG) {
			InventoryPlayer inventoryplayer1 = this.mc.thePlayer.inventory;

			if (inventoryplayer1.getItemStack() != null) {
				if (clickedButton == 0) {
					this.mc.thePlayer.dropPlayerItemWithRandomChoice(inventoryplayer1.getItemStack());
					this.mc.playerController.sendPacketDropItem(inventoryplayer1.getItemStack());
					inventoryplayer1.setItemStack(null);
				}

				if (clickedButton == 1) {
					ItemStack itemstack5 = inventoryplayer1.getItemStack().splitStack(1);
					this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemstack5);
					this.mc.playerController.sendPacketDropItem(itemstack5);

					if (inventoryplayer1.getItemStack().stackSize == 0) {
						inventoryplayer1.setItemStack(null);
					}
				}
			}
		} else if (slotIn == this.recycleBin && flag) {
			for (int j = 0; j < this.mc.thePlayer.inventoryContainer.getInventory().size(); ++j) {
				this.mc.playerController.sendSlotPacket(null, j);
			}
		} else if (showInv) {
			if (slotIn == this.recycleBin) {
				this.mc.thePlayer.inventory.setItemStack(null);
			} else if (clickType == DROP && slotIn != null && slotIn.getHasStack()) {
				ItemStack itemstack = slotIn.decrStackSize(clickedButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
				this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemstack);
				this.mc.playerController.sendPacketDropItem(itemstack);
			} else if (clickType == DROP && this.mc.thePlayer.inventory.getItemStack() != null) {
				this.mc.thePlayer.dropPlayerItemWithRandomChoice(this.mc.thePlayer.inventory.getItemStack());
				this.mc.playerController.sendPacketDropItem(this.mc.thePlayer.inventory.getItemStack());
				this.mc.thePlayer.inventory.setItemStack(null);
			} else {
				this.mc.thePlayer.inventoryContainer.slotClick(slotIn == null ? slotId : ((CreativeSlot) slotIn).slot.slotNumber, clickedButton, clickType, this.mc.thePlayer);
				this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
			}
		} else if (clickType != DRAG && slotIn.inventory == DUMMY) {
			InventoryPlayer inventoryplayer = this.mc.thePlayer.inventory;
			ItemStack itemstack1 = inventoryplayer.getItemStack();
			ItemStack itemstack2 = slotIn.getStack();

			if (clickType == HOTBAR) {
				if (itemstack2 != null && clickedButton >= 0 && clickedButton < 9) {
					ItemStack itemstack7 = itemstack2.copy();
					itemstack7.stackSize = itemstack7.getMaxStackSize();
					this.mc.thePlayer.inventory.setInventorySlotContents(clickedButton, itemstack7);
					this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
				}

				return;
			}

			if (clickType == PICK) {
				if (inventoryplayer.getItemStack() == null && slotIn.getHasStack()) {
					ItemStack itemstack6 = slotIn.getStack().copy();
					itemstack6.stackSize = itemstack6.getMaxStackSize();
					inventoryplayer.setItemStack(itemstack6);
				}

				return;
			}

			if (clickType == DROP) {
				if (itemstack2 != null) {
					ItemStack itemstack3 = itemstack2.copy();
					itemstack3.stackSize = clickedButton == 0 ? 1 : itemstack3.getMaxStackSize();
					this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemstack3);
					this.mc.playerController.sendPacketDropItem(itemstack3);
				}

				return;
			}

			if (itemstack1 != null && itemstack1.isItemEqual(itemstack2)) {
				if (clickedButton == 0) {
					if (flag) {
						itemstack1.stackSize = itemstack1.getMaxStackSize();
					} else if (itemstack1.stackSize < itemstack1.getMaxStackSize()) {
						++itemstack1.stackSize;
					}
				} else if (itemstack1.stackSize <= 1) {
					inventoryplayer.setItemStack(null);
				} else {
					--itemstack1.stackSize;
				}
			} else if (itemstack2 != null && itemstack1 == null) {
				inventoryplayer.setItemStack(ItemStack.copyItemStack(itemstack2));
				itemstack1 = inventoryplayer.getItemStack();

				if (flag) {
					itemstack1.stackSize = itemstack1.getMaxStackSize();
				}
			} else {
				inventoryplayer.setItemStack(null);
			}
		} else {
			this.inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, clickedButton, clickType, this.mc.thePlayer);

			if (Container.getDragEvent(clickedButton) == 2) {
				for (int i = 0; i < 9; ++i) {
					this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + i).getStack(), 36 + i);
				}
			} else if (slotIn != null) {
				ItemStack itemstack4 = this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
				this.mc.playerController.sendSlotPacket(itemstack4, slotIn.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
			}
		}
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		if (!this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
			return;
		}
		super.initGui();
		this.buttonList.clear();
		Keyboard.enableRepeatEvents(true);
		this.field_147059_E = new CreativeCrafting(this.mc);
		this.mc.thePlayer.inventoryContainer.onCraftGuiOpened(this.field_147059_E);
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		super.onGuiClosed();

		if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
			this.mc.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.field_147059_E);
		}

		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	private void setCurrentCreativeTab(CreativeTabs p_147050_1_) {
		ContainerCreativeItems c = (ContainerCreativeItems) this.inventorySlots;
		this.dragSplittingSlots.clear();
		c.itemList.clear();
		p_147050_1_.displayAllReleventItems(c.itemList);

		if (p_147050_1_ == CreativeTabs.tabInventory) {
			Container container = this.mc.thePlayer.inventoryContainer;

			if (this.field_147063_B == null) {
				this.field_147063_B = c.inventorySlots;
			}

			c.inventorySlots = new ArrayList<>();

			for (int j = 0; j < container.inventorySlots.size(); ++j) {
				Slot slot = new CreativeSlot(container.inventorySlots.get(j), j);
				c.inventorySlots.add(slot);

				if (j >= 5 && j < 9) {
					int j1 = j - 5;
					int k1 = j1 / 2;
					int l1 = j1 % 2;
					slot.xDisplayPosition = 9 + k1 * 54;
					slot.yDisplayPosition = 6 + l1 * 27;
				} else if (j >= 0 && j < 5) {
					slot.yDisplayPosition = -2000;
					slot.xDisplayPosition = -2000;
				} else if (j < container.inventorySlots.size()) {
					int k = j - 9;
					int l = k % 9;
					int i1 = k / 9;
					slot.xDisplayPosition = 9 + l * 18;

					if (j >= 36) {
						slot.yDisplayPosition = 112;
					} else {
						slot.yDisplayPosition = 54 + i1 * 18;
					}
				}
			}

			this.recycleBin = new Slot(DUMMY, 0, 173, 112);
			c.inventorySlots.add(this.recycleBin);
		} else if (showInv) {
			c.inventorySlots = this.field_147063_B;
			this.field_147063_B = null;
		}


		scrollTo(this.currentScroll = 0.0F);
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();
		if (i == 0) return;

		int j = ((ContainerCreativeItems) this.inventorySlots).itemList.size() / 9 - 5;

		if (i > 0) i = 1;
		if (i < 0) i = -1;

		this.currentScroll = (float) ((double) this.currentScroll - (double) i / (double) j);
		this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
		scrollTo(currentScroll);
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.recycleBin != null && this.isPointInRegion(this.recycleBin.xDisplayPosition, this.recycleBin.yDisplayPosition, 16, 16, mouseX, mouseY))
			this.drawCreativeTabHoveringText(Lang.format("inventory.binSlot"), mouseX, mouseY);

		drawGradientRect(0, 0, width, 150, 0xd0000000, 0);

		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		G.disableLighting();
	}

	/**
	 * Args : renderPartialTicks, mouseX, mouseY
	 */
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

	}

	public void scrollTo(float scrollPosition) {
		((ContainerCreativeItems) inventorySlots).scrollTo(scrollPosition);
	}

	class CreativeSlot extends Slot {

		private final Slot slot;

		public CreativeSlot(Slot slot, int id) {
			super(slot.inventory, id, 0, 0);
			this.slot = slot;
		}

		public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
			this.slot.onPickupFromSlot(playerIn, stack);
		}

		public boolean isItemValid(ItemStack stack) {
			return this.slot.isItemValid(stack);
		}

		public ItemStack getStack() {
			return this.slot.getStack();
		}

		public boolean getHasStack() {
			return this.slot.getHasStack();
		}

		public void putStack(ItemStack stack) {
			this.slot.putStack(stack);
		}

		public void onSlotChanged() {
			this.slot.onSlotChanged();
		}

		public int getSlotStackLimit() {
			return this.slot.getSlotStackLimit();
		}

		public int getItemStackLimit(ItemStack stack) {
			return this.slot.getItemStackLimit(stack);
		}

		public String getSlotTexture() {
			return this.slot.getSlotTexture();
		}

		public ItemStack decrStackSize(int amount) {
			return this.slot.decrStackSize(amount);
		}

		public boolean isHere(IInventory inv, int slotIn) {
			return this.slot.isHere(inv, slotIn);
		}

	}

}
