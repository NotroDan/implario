package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.Player;

@Getter
@RequiredArgsConstructor
public abstract class APlayerEvent implements IPlayerEvent{
    private final Player player;
}
