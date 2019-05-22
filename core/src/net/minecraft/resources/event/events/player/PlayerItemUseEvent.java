package net.minecraft.resources.event.events.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.event.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class PlayerItemUseEvent extends Event {

	private final EntityPlayer player;
	private final World world;
	private final BlockPos pos;
	private final EnumFacing side;
	private final float hitX, hitY, hitZ;
	private final ItemStack stack;
	private boolean used;

	public PlayerItemUseEvent(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		this.player = player;
		this.stack = stack;
		this.world = world;
		this.pos = pos;
		this.side = side;
		this.hitX = hitX;
		this.hitY = hitY;
		this.hitZ = hitZ;
	}

	public void setUsed() {
		used = true;
	}

	public World getWorld() {
		return world;
	}

	public BlockPos getPosition() {
		return pos;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public EnumFacing getSide() {
		return side;
	}

	public float getHitX() {
		return hitX;
	}

	public float getHitY() {
		return hitY;
	}

	public float getHitZ() {
		return hitZ;
	}

	public ItemStack getStack() {
		return stack;
	}

	public boolean isUsed() {
		return used;
	}

}
