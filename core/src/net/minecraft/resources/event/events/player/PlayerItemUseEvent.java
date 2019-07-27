package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@RequiredArgsConstructor
@Getter
@ToString
public class PlayerItemUseEvent extends PlayerEvent<PlayerItemUseEvent> {

	private final Player player;
	private final ItemStack stack;
	private final World world;
	private final BlockPos pos;
	private final EnumFacing side;
	private final float hitX, hitY, hitZ;

	@Setter
	private boolean used;

}
