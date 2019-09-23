package net.minecraft.resources.event.events.player;

import lombok.Getter;
import net.minecraft.entity.player.Player;
import net.minecraft.util.DamageSource;

public class PlayerDeathEvent extends APlayerСancelableEvent {
	@Getter
	private final DamageSource cause;

	public PlayerDeathEvent(Player player, DamageSource cause){
		super(player);
		this.cause = cause;
	}
}