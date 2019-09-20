package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.util.Vec3;

@Getter
public class PlayerMountMoveEvent extends PlayerMoveEvent {
	private final Entity mount;

	public PlayerMountMoveEvent(Player player, Entity mount, double srcX, double srcY, double srcZ, double dstX, double dstY, double dstZ){
		super(player, srcX, srcY, srcZ, dstX, dstY, dstZ);
		this.mount = mount;
	}
}
