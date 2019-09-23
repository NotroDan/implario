package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@Getter
public class PlayerItemUseEvent extends APlayerСancelableEvent {
	private final ItemStack stack;
	private final BlockPos pos;
	private final EnumFacing side;
	private final float hitX, hitY, hitZ;

	@Setter
	private boolean used;

	public PlayerItemUseEvent(Player player, ItemStack stack, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		super(player);
		this.stack = stack;
		this.pos = pos;
		this.side = side;
		this.hitX = hitX;
		this.hitY = hitY;
		this.hitZ = hitZ;
	}
}
