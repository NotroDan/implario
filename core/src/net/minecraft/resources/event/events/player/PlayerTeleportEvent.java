package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.Player;
import net.minecraft.util.Location;
import net.minecraft.util.Vec3d;

public class PlayerTeleportEvent extends APlayerСancelableEvent {
    @Getter
    @Setter
    private Location location;

    public PlayerTeleportEvent(Player player, Location location){
        super(player);
        this.location = location;
    }
}
