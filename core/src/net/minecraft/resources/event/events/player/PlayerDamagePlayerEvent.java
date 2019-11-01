package net.minecraft.resources.event.events.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.Player;
import net.minecraft.util.DamageSource;

@Getter
public class PlayerDamagePlayerEvent extends APlayerСancelableEvent{
    private final Player damager;
    @Setter
    private DamageSource source;
    @Setter
    private float amount;

    public PlayerDamagePlayerEvent(Player player, Player damager, DamageSource source, float amount){
        super(player);
        this.damager = damager;
        this.source = source;
        this.amount = amount;
    }
}
