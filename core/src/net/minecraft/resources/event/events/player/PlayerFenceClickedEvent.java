package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.Player;
import net.minecraft.resources.event.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@Getter
public class PlayerFenceClickedEvent extends APlayerEvent {
	private final World world;
	private final BlockPos pos;
	@Setter
	private boolean returnValue;

	public PlayerFenceClickedEvent(Player player, World world, BlockPos pos) {
		super(player);
		this.world = world;
		this.pos = pos;
	}
}
