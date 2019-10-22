package net.minecraft.resources.event.events.player;

import net.minecraft.entity.player.Player;

public class PlayerJoinEvent extends APlayerEvent{
    public PlayerJoinEvent(Player player){
        super(player);
    }
}
