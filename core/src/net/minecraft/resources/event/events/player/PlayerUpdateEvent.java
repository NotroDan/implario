package net.minecraft.resources.event.events.player;

import lombok.*;
import net.minecraft.entity.player.Player;

@ToString
@Getter
@RequiredArgsConstructor
public class PlayerUpdateEvent extends PlayerEvent<PlayerUpdateEvent> {

	private final Player player;

}
