package net.minecraft.world;

import net.minecraft.inventory.Inventory;

public interface LockableContainer extends Inventory, IInteractionObject {

	boolean isLocked();

	void setLockCode(LockCode code);

	LockCode getLockCode();

}
