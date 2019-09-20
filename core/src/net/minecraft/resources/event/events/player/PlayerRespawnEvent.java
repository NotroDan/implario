package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.Player;
import net.minecraft.util.BlockPos;

public class PlayerRespawnEvent extends APlayerEvent {
    @Setter
    @Getter
    private BlockPos pos;

    public PlayerRespawnEvent(Player player, BlockPos pos){
        super(player);
        this.pos = pos;
    }
}
