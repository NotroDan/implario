package net.minecraft.world;

import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public interface IInteractionObject extends IWorldNameable {

	Container createContainer(InventoryPlayer playerInventory, Player playerIn);

	String getGuiID();

}
