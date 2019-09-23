package net.minecraft.resources.event.events.player;

import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.Player;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.event.Event;

@Getter
public class ItemInteractForEntityEvent extends APlayerСancelableEvent {
	private final Item item;
	private final ItemStack stack;
	private final EntityLivingBase target;
	private boolean canBeUsed;

	public ItemInteractForEntityEvent(Player player, Item item, ItemStack stack, EntityLivingBase target) {
		super(player);
		this.item = item;
		this.stack = stack;
		this.target = target;
	}

	public void successUse() {
		canBeUsed = true;
	}
}
