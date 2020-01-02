package net.minecraft.inventory;

import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {

	private Inventory lowerChestInventory;
	private int numRows;

	public ContainerChest(Inventory playerInventory, Inventory chestInventory, Player player) {
		this.lowerChestInventory = chestInventory;
		this.numRows = chestInventory.getSizeInventory() / 9;
		chestInventory.openInventory(player);
		int i = (this.numRows - 4) * 18;

		for (int row = 0; row < this.numRows; ++row)
			for (int col = 0; col < 9; ++col)
				this.addSlotToContainer(new Slot(chestInventory, col + row * 9, 8 + col * 18, 18 + row * 18));

		for (int l = 0; l < 3; ++l) for (int j1 = 0; j1 < 9; ++j1) this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));

		for (int i1 = 0; i1 < 9; ++i1) this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
	}

	public boolean canInteractWith(Player playerIn) {
		return this.lowerChestInventory.isUseableByPlayer(playerIn);
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	public ItemStack transferStackInSlot(Player playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.numRows * 9) {
				if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true)) return null;
			} else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false)) return null;

			if (itemstack1.stackSize == 0) slot.putStack(null);
			else slot.onSlotChanged();
		}

		return itemstack;
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(Player playerIn) {
		super.onContainerClosed(playerIn);
		this.lowerChestInventory.closeInventory(playerIn);
	}

	/**
	 * Return this chest container's lower chest inventory.
	 */
	public Inventory getLowerChestInventory() {
		return this.lowerChestInventory;
	}

}
