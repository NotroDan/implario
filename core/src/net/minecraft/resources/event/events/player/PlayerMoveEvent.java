package net.minecraft.resources.event.events.player;

import lombok.*;
import net.minecraft.entity.player.Player;

@RequiredArgsConstructor
@Getter
@ToString
public class PlayerMoveEvent extends PlayerEvent<PlayerMoveEvent> {

	private final Player player;

	private final double srcX, srcY, srcZ;

	@ToString.Exclude
	private final double dstX, dstY, dstZ;

}
