package net.minecraft.resources.event.events.block;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.resources.event.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@Data
@EqualsAndHashCode (callSuper = false)
public class BlockDropEvent extends Event<BlockDropEvent> {

	private final World world;
	private final BlockPos position;
	private final IBlockState block;
	private final float chance;
	private final int fortune;
	private boolean isDefaultDropCancelled;

	public void cancelDefaultDrop() {
		isDefaultDropCancelled = true;
	}

}
