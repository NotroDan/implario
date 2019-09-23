package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.Player;

@Getter
public class PlayerTeleportPearlEvent extends APlayerСancelableEvent{
    @Setter
    private float damage;
    private final EntityEnderPearl pearl;

    public PlayerTeleportPearlEvent(Player player, EntityEnderPearl pearl, float damage) {
        super(player);
        this.damage = damage;
        this.pearl = pearl;
    }

    public PlayerTeleportPearlEvent(Player player, EntityEnderPearl pearl){
        this(player, pearl, 5.0F);
    }
}
