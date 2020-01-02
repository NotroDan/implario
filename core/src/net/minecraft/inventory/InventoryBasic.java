package net.minecraft.inventory;

import java.util.Arrays;

import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements Inventory {

	private String inventoryTitle;
	private int slotsCount;
	private ItemStack[] inventoryContents;
	private boolean hasCustomName;

	public InventoryBasic(String title, boolean customName, int slotCount) {
		this.inventoryTitle = title;
		this.hasCustomName = customName;
		this.slotsCount = slotCount;
		this.inventoryContents = new ItemStack[slotCount];
	}

	public InventoryBasic(IChatComponent title, int slotCount) {
		this(title.getUnformattedText(), true, slotCount);
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Override
	public ItemStack getStackInSlot(int index) {
		return index >= 0 && index < inventoryContents.length ? inventoryContents[index] : null;
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (inventoryContents[index] != null) {
			if (inventoryContents[index].stackSize <= count) {
				ItemStack itemstack1 = this.inventoryContents[index];
				inventoryContents[index] = null;
				markDirty();
				return itemstack1;
			}
			ItemStack itemstack = inventoryContents[index].splitStack(count);

			if (inventoryContents[index].stackSize == 0) {
				inventoryContents[index] = null;
			}

			markDirty();
			return itemstack;
		}
		return null;
	}

	public ItemStack func_174894_a(ItemStack stack) {
		ItemStack itemstack = stack.copy();

		for (int i = 0; i < this.slotsCount; ++i) {
			ItemStack itemstack1 = this.getStackInSlot(i);

			if (itemstack1 == null) {
				this.setInventorySlotContents(i, itemstack);
				this.markDirty();
				return null;
			}

			if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
				int j = Math.min(this.getInventoryStackLimit(), itemstack1.getMaxStackSize());
				int k = Math.min(itemstack.stackSize, j - itemstack1.stackSize);

				if (k > 0) {
					itemstack1.stackSize += k;
					itemstack.stackSize -= k;

					if (itemstack.stackSize <= 0) {
						this.markDirty();
						return null;
					}
				}
			}
		}

		if (itemstack.stackSize != stack.stackSize) {
			this.markDirty();
		}

		return itemstack;
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (inventoryContents[index] != null) {
			ItemStack itemstack = inventoryContents[index];
			inventoryContents[index] = null;
			return itemstack;
		}
		return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventoryContents[index] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();

		markDirty();
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return slotsCount;
	}

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	@Override
	public String getName() {
		return inventoryTitle;
	}

	/**
	 * Returns true if this thing is named
	 */
	@Override
	public boolean hasCustomName() {
		return hasCustomName;
	}

	/**
	 * Sets the name of this inventory. This is displayed to the client on opening.
	 */
	public void setCustomName(String inventoryTitleIn) {
		hasCustomName = true;
		inventoryTitle = inventoryTitleIn;
	}

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username in chat
	 */
	@Override
	public IChatComponent getDisplayName() {
		return hasCustomName() ? new ChatComponentText(getName()) : new ChatComponentTranslation(getName());
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
	 * hasn't changed and skip it.
	 */
	@Override
	public void markDirty() {}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUseableByPlayer(Player player) {
		return true;
	}

	@Override
	public void openInventory(Player player) {}

	@Override
	public void closeInventory(Player player) {}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		Arrays.fill(inventoryContents, null);
	}
}
