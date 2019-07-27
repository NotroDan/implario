package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;

@Data
@EqualsAndHashCode (callSuper = false)
public class PlayerItemDropEvent extends PlayerEvent<PlayerItemDropEvent> {

	private final Player player;
	private final ItemStack droppedItem;
	private final boolean dropAround;
	private final boolean traceItem;

}
