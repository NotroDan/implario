package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.player.Player;
import net.minecraft.resources.event.Event;

@Data
@EqualsAndHashCode (callSuper = false)
public class PlayerLeaveDisconnect extends Event<PlayerLeaveDisconnect> {

	private final Player player;

}
