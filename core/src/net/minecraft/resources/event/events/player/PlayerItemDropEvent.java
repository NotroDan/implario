package net.minecraft.resources.event.events.player;

import lombok.Getter;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;

@Getter
public class PlayerItemDropEvent extends APlayerСancelableEvent	{
	private final ItemStack droppedItem;

	public PlayerItemDropEvent(Player player, ItemStack droppedItem){
		super(player);
		this.droppedItem = droppedItem;
	}
}
