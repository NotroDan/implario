package net.minecraft.resources.event.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.Player;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.event.Event;

public class ItemInteractForEntityEvent extends Event {

	private final Item item;
	private final ItemStack stack;
	private final Player player;
	private final EntityLivingBase target;
	private boolean canBeUsed;

	public ItemInteractForEntityEvent(Item item, ItemStack stack, Player playerIn, EntityLivingBase target) {
		this.item = item;
		this.stack = stack;
		this.player = playerIn;
		this.target = target;
	}

	public Player getPlayer() {
		return player;
	}

	public EntityLivingBase getTarget() {
		return target;
	}

	public Item getItem() {
		return item;
	}

	public ItemStack getStack() {
		return stack;
	}

	public boolean canBeUsed() {
		return canBeUsed;
	}

	public void successUse() {
		canBeUsed = true;
	}

}
