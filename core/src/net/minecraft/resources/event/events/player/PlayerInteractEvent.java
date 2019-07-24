package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@Data
@EqualsAndHashCode (callSuper = false)
public class PlayerInteractEvent extends PlayerEvent<PlayerInteractEvent> {

	private final EntityPlayer player;
	private final World world;
	private final ItemStack stack;
	private final BlockPos pos;
	private final IBlockState block;
	private final EnumFacing side;
	private final float hitX, hitY, hitZ;
	private boolean cancelled;
	private boolean sendToServer = true;
	private boolean armSwing = true;

}
