package net.minecraft.resources.event.events.player;

import lombok.Getter;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.Player;

@Getter
public class TrackerUpdateEvent extends APlayerEvent {
	private final EntityTrackerEntry entry;

	public TrackerUpdateEvent(Player player, EntityTrackerEntry entry){
		super(player);
		this.entry = entry;
	}
}
