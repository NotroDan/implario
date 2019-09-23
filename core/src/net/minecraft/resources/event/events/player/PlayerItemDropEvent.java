package net.minecraft.resources.event.events.player;

import lombok.Getter;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;

@Getter
public class PlayerItemDropEvent extends APlayerСancelableEvent	{
	private final ItemStack droppedItem;
	private final boolean dropAround;
	private final boolean traceItem;

	public PlayerItemDropEvent(Player player, ItemStack droppedItem, boolean dropAround, boolean traceItem){
		super(player);
		this.droppedItem = droppedItem;
		this.dropAround = dropAround;
		this.traceItem = traceItem;
	}
}
