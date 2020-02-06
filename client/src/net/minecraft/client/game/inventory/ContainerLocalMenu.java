package net.minecraft.client.game.inventory;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.LockableContainer;
import net.minecraft.world.LockCode;

public class ContainerLocalMenu extends InventoryBasic implements LockableContainer {
	private final String guiID;
	private final Map<Integer, Integer> fields = new HashMap<>();

	public ContainerLocalMenu(String id, IChatComponent title, int slotCount) {
		super(title, slotCount);
		this.guiID = id;
	}

	public int getField(int id) {
		return fields.getOrDefault(id, 0);
	}

	public void setField(int id, int value) {
		fields.put(id, value);
	}

	public int getFieldCount() {
		return fields.size();
	}

	public boolean isLocked() {
		return false;
	}

	public void setLockCode(LockCode code) {}

	public LockCode getLockCode() {
		return LockCode.EMPTY_CODE;
	}

	public String getGuiID() {
		return this.guiID;
	}

	public Container createContainer(InventoryPlayer playerInventory, Player playerIn) {
		throw new UnsupportedOperationException();
	}

}
