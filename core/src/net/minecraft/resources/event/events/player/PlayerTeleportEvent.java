package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.Player;
import net.minecraft.util.Vec3;

public class PlayerTeleportEvent extends APlayerСancelableEvent {
    @Getter
    @Setter
    private Vec3 vec;

    public PlayerTeleportEvent(Player player, Vec3 vec){
        super(player);
        this.vec = vec;
    }
}
