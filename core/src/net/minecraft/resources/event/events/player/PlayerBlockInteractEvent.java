package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@Getter
public class PlayerBlockInteractEvent extends PlayerItemUseEvent {
	@Setter
	private boolean sendToServer = true, armSwing = true, airUse;
	private final IBlockState blockState;

	public PlayerBlockInteractEvent(Player player, ItemStack stack, BlockPos pos, IBlockState state, EnumFacing side,
									float hitX, float hitY, float hitZ){
		super(player, stack, pos, side, hitX, hitY, hitZ, false);
		this.blockState = state;
	}
}
