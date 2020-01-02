package net.minecraft.inventory;

import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCraftResult implements Inventory {
	private ItemStack stackResult;

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return 1;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	public ItemStack getStackInSlot(int index) {
		return stackResult;
	}

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getName() {
		return "Result";
	}

	/**
	 * Returns true if this thing is named
	 */
	public boolean hasCustomName() {
		return false;
	}

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username in chat
	 */
	public IChatComponent getDisplayName() {
		return (IChatComponent) (this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName(), new Object[0]));
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	public ItemStack decrStackSize(int index, int count) {
		if (stackResult != null) {
			ItemStack itemstack = stackResult;
			stackResult = null;
			return itemstack;
		}
		return null;
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeStackFromSlot(int index) {
		if (stackResult != null) {
			ItemStack itemstack = stackResult;
			stackResult = null;
			return itemstack;
		}
		return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	public void setInventorySlotContents(int index, ItemStack stack) {
		stackResult = stack;
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
	 */
	public int getInventoryStackLimit() {
		return 64;
	}

	public void markDirty() {}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	public boolean isUseableByPlayer(Player player) {
		return true;
	}

	public void openInventory(Player player) {}

	public void closeInventory(Player player) {}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {
		stackResult = null;
	}
}
