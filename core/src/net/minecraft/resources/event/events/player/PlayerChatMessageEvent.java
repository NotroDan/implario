package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.Player;

@Getter
@Setter
public class PlayerChatMessageEvent extends APlayerСancelableEvent{
    private String message;

    public PlayerChatMessageEvent(Player player, String message){
        super(player);
        this.message = message;
    }
}
