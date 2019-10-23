package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.Player;

public class PlayerFoodChangeEvent extends APlayerEvent{
    @Getter
    @Setter
    private int food;

    public PlayerFoodChangeEvent(Player player, int food){
        super(player);
        this.food = food;
    }
}
