package net.minecraft.inventory;

import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;

public class InventoryCrafting implements IInventory {
	/**
	 * List of the stacks in the crafting matrix.
	 */
	private final ItemStack[] stackList;

	/**
	 * the width of the crafting inventory
	 */
	private final int inventoryWidth;
	private final int inventoryHeight;

	/**
	 * Class containing the callbacks for the events on_GUIClosed and on_CraftMaxtrixChanged.
	 */
	private final Container eventHandler;

	public InventoryCrafting(Container eventHandlerIn, int width, int height) {
		int i = width * height;
		stackList = new ItemStack[i];
		eventHandler = eventHandlerIn;
		inventoryWidth = width;
		inventoryHeight = height;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return stackList.length;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Override
	public ItemStack getStackInSlot(int index) {
		return index >= getSizeInventory() ? null : stackList[index];
	}

	/**
	 * Returns the itemstack in the slot specified (Top left is 0, 0). Args: row, column
	 */
	public ItemStack getStackInRowAndColumn(int row, int column) {
		return row >= 0 && row < inventoryWidth &&
				column >= 0 && column <= inventoryHeight ?
				getStackInSlot(row + column * inventoryWidth) : null;
	}

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	@Override
	public String getName() {
		return "container.crafting";
	}

	/**
	 * Returns true if this thing is named
	 */
	@Override
	public boolean hasCustomName() {
		return false;
	}

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username in chat
	 * Изначально было hasCustomName() ? new ChatComponentText(getName()) : new ChatComponentTranslation(getName())
	 * Но этот класс никто не наследует и hasCustonName всегда false
	 */
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentTranslation(getName());
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (stackList[index] != null) {
			ItemStack itemstack = stackList[index];
			stackList[index] = null;
			return itemstack;
		}
		return null;
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (stackList[index] != null) {
			if (stackList[index].stackSize <= count) {
				ItemStack itemstack1 = this.stackList[index];
				stackList[index] = null;
				eventHandler.onCraftMatrixChanged(this);
				return itemstack1;
			}
			ItemStack itemstack = stackList[index].splitStack(count);

			if (stackList[index].stackSize == 0) {
				stackList[index] = null;
			}

			eventHandler.onCraftMatrixChanged(this);
			return itemstack;
		}
		return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		stackList[index] = stack;
		eventHandler.onCraftMatrixChanged(this);
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
		Arrays.fill(stackList, null);
	}

	public int getHeight() {
		return inventoryHeight;
	}

	public int getWidth() {
		return inventoryWidth;
	}
}
