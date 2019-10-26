package net.minecraft.resources.event.events.player;

import lombok.Getter;
import net.minecraft.entity.player.Player;
import net.minecraft.util.BlockPos;

public class PlayerBlockClickEvent extends APlayerСancelableEvent{
    @Getter
    private final BlockPos pos;

    public PlayerBlockClickEvent(Player player, BlockPos pos) {
        super(player);
        this.pos = pos;
    }
}
