package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.entity.player.EntityPlayer;

@ToString
@Getter
@RequiredArgsConstructor
public class PlayerUpdateEvent extends PlayerEvent<PlayerUpdateEvent> {

	private final EntityPlayer player;

}
