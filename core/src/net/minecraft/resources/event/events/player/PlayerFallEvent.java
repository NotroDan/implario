package net.minecraft.resources.event.events.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.Player;

public class PlayerFallEvent extends APlayerСancelableEvent {
	@Setter
	@Getter
	private float distance, damageMultiplier;

	public PlayerFallEvent(Player player, float distance, float damageMultiplier){
		super(player);
		this.distance = distance;
		this.damageMultiplier = damageMultiplier;
	}
}
