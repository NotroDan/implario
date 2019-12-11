package net.minecraft.inventory;

public interface InventoryListener {
	/**
	 * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
	 */
	void onInventoryChanged(InventoryBasic inventory);
}
