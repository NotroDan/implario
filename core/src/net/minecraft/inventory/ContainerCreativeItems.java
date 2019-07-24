package net.minecraft.inventory;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Group.Unit;
import net.minecraft.item.Groups;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.item.Groups.duoW;
import static net.minecraft.item.Groups.unoW;

public class ContainerCreativeItems extends Container {


	public static final IInventory DUMMY = new InventoryBasic("Болванка", true, 200);
	public final int left;

	public List<ItemStack> itemList = new ArrayList<>();

	public ContainerCreativeItems(EntityPlayer p) {
		InventoryPlayer inventoryplayer = p.inventory;


		int duo = unoW * 18 + 10;
		left = unoW * 9 + 5 + duoW * 9;

		int i = 9;
		for (Groups.Coord c : Groups.uno) {
			Iterator<Unit> it = c.getGroup().getElements().iterator();
			for (int y = 0; y < c.getGroup().getHeight(); y++) {
				for (int s = 0; s < c.getGroup().getWidth(); s++) {
					Unit u = it.next();
					if (u.item == null) continue;
					addSlotToContainer(new ScrollSlot(DUMMY, i, s * 18, y * 18 + c.getY()));
					itemList.add(u.getItem());
					DUMMY.setInventorySlotContents(i, u.getItem());
					i++;
				}
			}
		}
		for (Groups.Coord c : Groups.duo) {
			Iterator<Unit> it = c.getGroup().getElements().iterator();
			for (int y = 0; y < c.getGroup().getHeight(); y++) {
				for (int s = 0; s < c.getGroup().getWidth(); s++) {
					Unit u = it.next();
					if (u.item == null) continue;
					addSlotToContainer(new ScrollSlot(DUMMY, i, duo + s * 18, y * 18 + c.getY()));
					itemList.add(u.getItem());
					DUMMY.setInventorySlotContents(i, u.getItem());
					i++;
				}
			}
		}

		int hotbar = duo / 2 + duoW * 9 - 81;

		for (int k = 0; k < 9; ++k) {
			this.addSlotToContainer(new Slot(inventoryplayer, k, hotbar + k * 18, 112));
		}

	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public boolean func_148328_e() {
		return this.itemList.size() > 45;
	}

	protected void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn) {
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		if (index < this.inventorySlots.size() - 9 || index >= this.inventorySlots.size()) return null;

		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) slot.putStack(null);

		return null;
	}

	public boolean canMergeSlot(ItemStack stack, Slot slot) {
		return slot.getSlotIndex() < 9;
	}

	public boolean canDragIntoSlot(Slot s) {
		return s.inventory instanceof InventoryPlayer || s.getSlotIndex() < 9 && s.xDisplayPosition <= 162;
	}


	public void scrollTo(float scroll) {
		int offset = (int) (scroll * Groups.height);

		for (Slot slot : inventorySlots)
			if (slot instanceof ScrollSlot) slot.yDisplayPosition = ((ScrollSlot) slot).baseY - offset;

	}

	private static class ScrollSlot extends Slot {

		public int baseY;

		public ScrollSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
			baseY = yPosition;
		}

	}

}
