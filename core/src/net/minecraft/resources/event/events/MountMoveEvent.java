package net.minecraft.resources.event.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.event.Event;

@RequiredArgsConstructor
@Getter
@ToString
public class MountMoveEvent extends Event<MountMoveEvent> {

	private final EntityPlayer player;
	private final Entity mount;

	private final double srcX, srcY, srcZ;

	@ToString.Exclude
	private final double dstX, dstY, dstZ;

}
